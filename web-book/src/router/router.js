import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/Login.vue'
import AdminManage from "@/admin/AdminManage.vue";
import Register from "@/Register.vue";
import UserManage from "@/admin/UserManage.vue";
import IndexPage from "@/admin/IndexPage.vue";
import HomePage from "@/admin/HomePage.vue";
import SearchPage from "@/user/home/SearchPage.vue";
import ContainerPage from "@/user/home/ContainerPage.vue";
import HomeBar from "@/user/home/HomeBar.vue";
import BookPage from "@/user/BookPage.vue";
import CategoryPage from "@/user/CategoryPage.vue";
import UserCenter from "@/user/UserCenter.vue";
import ReaderHome from "@/user/ReaderHome.vue";
import ConfirmPage from "@/user/ConfirmPage.vue";
import PayPage from "@/user/PayPage.vue";
import PayResult from "@/user/PayResult.vue";
import OrderPage from "@/user/OrderPage.vue";
import OrderDetail from "@/user/OrderDetail.vue";
import CartPage from "@/user/CartPage.vue";
import BookManage from "@/admin/BookManage.vue";
import CategoryManage from "@/admin/CategoryManage.vue";
import OrderManage from "@/admin/OrderManage.vue";
import ReviewManage from "@/admin/ReviewManage.vue";
import MyReview from "@/user/MyReview.vue";
import MyReservation from "@/user/MyReservation.vue";
import AnnouncementManage from "@/admin/AnnouncementManage.vue";
import AnnouncementPage from "@/user/AnnouncementPage.vue";
import GuestbookManage from "@/admin/GuestbookManage.vue";
import GuestbookPage from "@/user/GuestbookPage.vue";
import RecordManage from "@/admin/RecordManage.vue";


Vue.use(VueRouter);

const routes = [
    //前台
    {
        path: "/",
        name:"前台",
        component: ContainerPage,
        children: [
            //公共路由
            { path: '/', name: '前台首页', component: HomeBar },
            { path: '/search', name:"搜索", component: SearchPage },
            { path: '/book', name:'书籍详情页', component: BookPage },
            { path: '/category', name:'分类页', component: CategoryPage },
            { path: '/announcement', name:'公告', component:AnnouncementPage},
            { path: '/reader', name:'读者之家', component: ReaderHome},
            { path:'/cart', name:'购物车', component:CartPage},
            // 需要登录的用户路由
            {
                path:'/center',
                name: '个人中心',
                component: UserCenter,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path: '/confirm',
                component: ConfirmPage,
                props: route => ({
                    oiids: route.query.oiids,
                    type: route.query.type
                }),
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path:'/pay',
                name:'支付',
                component: PayPage,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path:'/pay-result',
                name:'支付result',
                component: PayResult,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path:'/order',
                name:'我的订单',
                component: OrderPage,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path:'/order/detail/:oid',
                name:'订单详情',
                component: OrderDetail,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path:'/myreview',
                name:'我的评论',
                component:MyReview,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path: '/myreservation',
                name:'我的预定',
                component:MyReservation,
                meta: { requiresAuth: true, role: 1 }
            },
            {
                path:'/guestbook',
                name: "留言",
                component:GuestbookPage,
                meta: { requiresAuth: true, role: 1 }
            }
        ]
    },

    //通用
    {
        path: '/login',
        name: '登录',
        component: Login,
        meta: { guestOnly: true } // 仅允许未登录用户访问
    },
    {
        path: '/register',
        name: '注册',
        component: Register,
        meta: { guestOnly: true } // 仅允许未登录用户访问
    },

    //后台
    {
        path:"/admin",
        name: '后台首页',
        redirect:"/admin/home",
        component: IndexPage,
        meta: { requiresAuth: true, role: 0 }, // 管理员权限
        children:[
            {
                path:"home",
                name:"HomePage",
                component:HomePage,
            },
            {
                path:"ad",
                name:"管理员管理",
                component:AdminManage,
            },
            {
                path:"user",
                name: "用户管理",
                component:UserManage,
            },
            {
                path:"book",
                name: "书籍管理",
                component:BookManage,
            },
            {
                path:"category",
                name: "分类管理",
                component:CategoryManage,
            },
            {
                path:"order",
                name: "订单管理",
                component:OrderManage,
            },
            {
                path:"review",
                name: "评论管理",
                component:ReviewManage,
            },
            {
                path:"announcement",
                name: "公告管理",
                component:AnnouncementManage,
            },
            {
                path:"guestbook",
                name: "留言管理",
                component:GuestbookManage,
            },
            {
                path:"record",
                name: "出入库记录管理",
                component:RecordManage,
            }
        ]
    }

]

const router = new VueRouter({
    mode: 'history',  // 可选，去除URL中的#
    routes,
})

//全局路由守卫
router.beforeEach((to, from, next) => {
    // 获取路由元信息
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const guestOnly = to.matched.some(record => record.meta.guestOnly)
    // 获取用户角色信息
    const currentUser = JSON.parse(sessionStorage.getItem('CurUser'))
    const isLoggedIn = currentUser !== null
    const userRole = isLoggedIn ? currentUser.roleId : null
    // 处理需要认证的路由
    if (requiresAuth) {
        if (!isLoggedIn) {
            // 未登录时跳转到登录页，并记录目标路径
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            })
        } else {
            // 获取路由需要的角色（从最深层的路由开始查找）
            const requiredRole = to.matched.slice().reverse().find(r => r.meta.role)?.meta.role
            // 验证角色权限
            if (requiredRole !== undefined && userRole !== requiredRole) {
                // 角色不匹配时根据当前角色重定向
                const redirectPath = userRole === 0 ? '/admin' : '/'
                next(redirectPath)
                // 可以在此处添加权限提示，但需要全局消息组件支持
            } else {
                next()
            }
        }
    }
    // 处理仅允许游客访问的路由
    else if (guestOnly) {
        if (isLoggedIn) {
            // 已登录用户访问登录/注册页时重定向到首页
            const redirectPath = userRole === 0 ? '/admin' : '/'
            next(redirectPath)
        } else {
            next()
        }
    }
    // 公共路由直接放行
    else {
        next()
    }
})

//解决路由错误问题
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to){
    return VueRouterPush.call(this,to).catch(err=>err)
}

export default router