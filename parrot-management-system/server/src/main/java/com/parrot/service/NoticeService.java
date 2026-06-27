package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.NoticeQueryDTO;
import com.parrot.dto.NoticeSaveDTO;
import com.parrot.vo.NoticeVO;

import java.util.List;

/**
 * 公告和饲养知识业务。
 */
public interface NoticeService {

    PageResult<NoticeVO> page(NoticeQueryDTO query);

    NoticeVO add(NoticeSaveDTO dto);

    NoticeVO update(Long id, NoticeSaveDTO dto);

    void delete(Long id);

    List<NoticeVO> publicList(NoticeQueryDTO query);

    NoticeVO publicDetail(Long id);
}
