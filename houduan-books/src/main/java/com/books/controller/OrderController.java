package com.books.controller;


import com.books.common.QueryPageParam;
import com.books.common.Result;
import com.books.entity.*;
import com.books.mapper.OrderMapper;
import com.books.service.BookService;
import com.books.service.OrderItemService;
import com.books.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@CrossOrigin(origins = "http://localhost:8080") // 允许前端域名
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource private OrderService orderService;
    @Resource private BookService bookService;
    @Resource private OrderItemService orderItemService;
    @Resource private OrderMapper orderMapper;

    //获取订单列表
    @GetMapping("/list")
    public Result getOrderList( HttpSession session, HttpServletRequest request) {
        //获取用户ID
        Integer userId = (Integer) session.getAttribute("userId");
        List<OrderVO> orders = orderService.getOrderList(userId);
        return Result.suc(orders);
    }

    // 删除订单接口
    @PostMapping("/delete")
    public Result deleteOrder(@RequestBody Map<String, Integer> params, HttpSession session) {
        //获取用户信息
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.fail("请先登录");
        //获取订单信息
        Integer oid = params.get("oid");
        Order order = orderService.getById(oid);
        if (order == null) return Result.fail("订单不存在");
        //验证权限
        if (!order.getUserId().equals(user.getId())) {
            return Result.fail("无权操作此订单");
        }
        //验证订单状态
        if (!Arrays.asList("已完成", "已取消").contains(order.getStatus())) {
            return Result.fail("只有已完成或已取消的订单可删除");
        }
        try {
            //删除关联订单项
            orderItemService.deleteByOrderId(oid);
            // 删除订单
            orderService.removeById(oid);
            return Result.suc("删除成功");
        } catch (Exception e) {
            log.error("订单删除失败：", e);
            return Result.fail("删除失败");
        }
    }

    // 订单详情接口
    @GetMapping("/detail/{oid}")
    public Result getOrderDetail(@PathVariable Integer oid) {
        OrderVO order = orderService.getOrderDetail(oid);
        return order != null ? Result.suc(order) : Result.fail("订单不存在");
    }

    // 取消订单接口
    @PostMapping("/cancel")
    public Result cancelOrder(@RequestBody Map<String, Integer> params) {
        Integer oid = params.get("oid");
        Order order = orderService.getById(oid);

        if (!"待付款".equals(order.getStatus())) {
            return Result.fail("当前状态不可取消");
        }
        order.setStatus("已取消");
        orderService.updateById(order);
        return Result.suc();
    }

    // 退款接口
    public static final Set<String> REFUNDABLE_STATUS = Set.of("已付款", "已发货");
    @PostMapping("/refund")
    public Result refundOrder(@RequestBody Map<String, Integer> params) {
        Integer oid = params.get("oid");
        Order order = orderService.getById(oid);
        // 校验订单状态和支付时间
        if (!REFUNDABLE_STATUS.contains(order.getStatus())) {
            return Result.fail("仅[已付款]/[已发货]状态可申请退款");
        }
        // 恢复库存
        orderService.restoreStock(oid);
        order.setStatus("已取消");
        orderService.updateById(order);
        return Result.suc("退款成功");
    }

    // 自动完成订单
    @PostMapping("/auto-complete")
    public Result autoCompleteOrder(@RequestBody Map<String, Integer> params) {
        Integer oid = params.get("oid");
        Order order = orderService.getById(oid);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if ("已发货".equals(order.getStatus())) {
            order.setStatus("已完成");
            orderService.updateById(order);
            log.info("前端触发订单状态更新：ID={}", oid);
        }
        return Result.suc();
    }

    // 立即购买
    @PostMapping("/buy")
    public Result buy(@RequestParam Integer pid, @RequestParam Integer num, HttpSession session) {
        // 1. 验证登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        // 2. 获取书籍信息
        Book book = bookService.getById(pid);
        if (book == null || book.getStatus() == 0) {
            return Result.fail("商品已下架或不存在");
        }
        // 3. 库存校验
        if (book.getStock() < num) {
            return Result.fail("库存不足，当前库存：" + book.getStock());
        }
        // 4. 计算折扣价格
        BigDecimal finalPrice = calculateDiscountPrice(book.getPrice(), book.getStock());
        // 5. 创建订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setBookId(pid);
        orderItem.setQuantity(num);
        orderItem.setPrice(finalPrice);
        orderItemService.save(orderItem);
        log.info("创建订单项成功，ID：{}，order_id：{}",
                orderItem.getId(),
                orderItem.getOrderId());

        return Result.suc(orderItem.getId());
    }

    @PostMapping("/batchBuy")
    public Result batchBuy(@RequestBody List<Map<String, Integer>> items, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }

        List<Integer> oiids = new ArrayList<>();
        for (Map<String, Integer> item : items) {
            Integer bookId = item.get("pid");
            Integer quantity = item.get("num");

            // 校验书籍状态
            Book book = bookService.getById(bookId);
            if (book == null || book.getStatus() == 0) {
                return Result.fail("商品[" + bookId + "]已下架");
            }
            if (book.getStock() < quantity) {
                return Result.fail("商品[" + book.getName() + "]库存不足");
            }

            // 计算折扣
            BigDecimal finalPrice = calculateDiscountPrice(book.getPrice(), book.getStock());

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(bookId);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(finalPrice);
            orderItemService.save(orderItem);
            oiids.add(orderItem.getId());
        }
        return Result.suc(oiids);
    }

    // 折扣计算逻辑（与前端一致）
    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice, Integer stock) {
        if (stock > 25) {
            return originalPrice.multiply(new BigDecimal("0.7"));
        } else if (stock >= 20) {
            return originalPrice.multiply(new BigDecimal("0.8"));
        } else if (stock >= 15) {
            return originalPrice.multiply(new BigDecimal("0.9"));
        }
        return originalPrice;
    }

    //查询数据
    @GetMapping("/confirm")
    public Result getConfirmInfo(@RequestParam Integer oiid) {
        OrderItem orderItem = orderItemService.getById(oiid);
        if (orderItem == null) {
            return Result.fail("订单项不存在");
        }
        Book book = bookService.getById(orderItem.getBookId());
        if (book == null) {
            return Result.fail("书籍不存在");
        }
        ConfirmVO vo = new ConfirmVO();
        vo.setBook(book);
        vo.setOrderItem(orderItem);
        vo.setOriginalPrice(book.getPrice());
        vo.setDiscountPrice(orderItem.getPrice());

        return Result.suc(vo);
    }
    // 新增批量确认接口
    @PostMapping("/confirmBatch")
    public Result getBatchConfirmInfo(@RequestBody Map<String, Object> params) {
        //参数校验
        if (!params.containsKey("oiids")) {
            return Result.fail("缺少必要参数");
        }
        //类型转换
        List<Integer> oiids;
        try {
            oiids = (List<Integer>) params.get("oiids");
        } catch (ClassCastException e) {
            return Result.fail("参数格式错误");
        }
        //获取订单项信息
        List<ConfirmVO> vos = new ArrayList<>();
        for (Integer oiid : oiids) {
            OrderItem item = orderItemService.getById(oiid);
            if (item == null) continue;

            Book book = bookService.getById(item.getBookId());
            ConfirmVO vo = new ConfirmVO();
            vo.setBook(book);
            vo.setOrderItem(item);
            vo.setOriginalPrice(book.getPrice());
            vo.setDiscountPrice(item.getPrice());
            vos.add(vo);
        }
        return Result.suc(vos);
    }

    // 修改创建订单接口支持多商品
    @PostMapping("/create")
    public Result createOrder(@RequestBody OrderCreateDTO dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.fail("请先登录");

        // 获取所有订单项
        List<OrderItem> items = dto.getOiids().stream()
                .map(oiid -> {
                    OrderItem item = orderItemService.getById(oiid);
                    if (item == null) {
                        throw new RuntimeException("无效的订单项ID：" + oiid);
                    }
                    return item;
                })
                .toList();
        // 计算总金额
        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 创建订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(total);
        order.setStatus("待付款");
        order.setAddress(dto.getAddress());
        order.setRecipient(dto.getRecipient());
        order.setTel(dto.getTel());
        order.setCreatetime(LocalDateTime.now());
        orderService.save(order);
        // 关联订单项并扣库存
        items.forEach(item -> {
            // 关联订单ID
            item.setOrderId(order.getId());
            orderItemService.updateById(item);
            // 扣减库存
            Book book = bookService.getById(item.getBookId());
            if (book.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品[" + book.getName() + "]库存不足");
            }
            book.setStock(book.getStock() - item.getQuantity());
            bookService.updateById(book);
        });
        return Result.suc(Map.of("oid", order.getId(), "total", total));
    }


    //支付接口
    @PostMapping("/pay")
    public Result pay(@RequestBody Map<String, String> params, HttpSession session) {
        // 1. 验证登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        Integer oid = Integer.parseInt(params.get("oid"));
        return orderService.payOrder(oid);
    }

    //更新地址等数据接口
    @PostMapping("/update")
    public Result updateOrder(@RequestBody OrderVO dto, HttpSession session) {
        //获取用户ID
        // 1. 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.fail("请先登录");
        // 2. 验证订单存在性
        Order order = orderService.getById(dto.getId());
        if(order == null) return Result.fail("订单不存在");
        // 3. 权限验证
        if(!order.getUserId().equals(user.getId())) return Result.fail("无权操作");
        // 4. 状态验证
        if(!"待付款".equals(order.getStatus())) return Result.fail("当前状态不可修改");
        // 5. 更新字段
        order.setAddress(dto.getAddress());
        order.setRecipient(dto.getRecipient());
        order.setTel(dto.getTel());
        orderService.updateById(order);

        return Result.suc("修改成功");
    }

    //admin
    @PostMapping("/page")
    //订单分页查询
    public Result page(@RequestBody QueryPageParam queryPageParam) {
        return orderService.pageQuery(queryPageParam);
    }

    @PostMapping("/acancel/{orderId}")
    //取消订单
    public Result cancelOrder(@PathVariable Integer orderId) {
        return orderService.cancelOrder(orderId);
    }

    // 发货状态
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Order order) {
        return orderService.updateStatus(order.getId(),order.getStatus())
                ? Result.suc()
                : Result.fail();
    }
}
