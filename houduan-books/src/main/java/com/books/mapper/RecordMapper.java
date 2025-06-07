package com.books.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.books.entity.Record;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.books.entity.RecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {


    IPage<RecordVO> selectRecordPage(Page<RecordVO> page, @Param(Constants.WRAPPER) QueryWrapper<Record> wrapper);
}
