package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约咨询 Mapper。
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
