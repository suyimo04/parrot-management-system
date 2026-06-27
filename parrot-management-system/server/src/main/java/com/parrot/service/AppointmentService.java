package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.AppointmentHandleDTO;
import com.parrot.dto.AppointmentQueryDTO;
import com.parrot.dto.AppointmentSubmitDTO;
import com.parrot.vo.AppointmentVO;

import java.util.List;

/**
 * 预约咨询业务。
 */
public interface AppointmentService {

    AppointmentVO submit(AppointmentSubmitDTO dto);

    List<AppointmentVO> myList(String status);

    void cancelMy(Long id);

    PageResult<AppointmentVO> page(AppointmentQueryDTO query);

    AppointmentVO confirm(Long id, AppointmentHandleDTO dto);

    AppointmentVO complete(Long id, AppointmentHandleDTO dto);

    AppointmentVO reject(Long id, AppointmentHandleDTO dto);

    AppointmentVO cancelByAdmin(Long id, AppointmentHandleDTO dto);
}
