package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.dto.NoticeQueryDTO;
import com.parrot.dto.NoticeSaveDTO;
import com.parrot.entity.Notice;
import com.parrot.entity.SysUser;
import com.parrot.mapper.NoticeMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.NoticeService;
import com.parrot.vo.NoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 公告知识实现，已发布内容才会出现在前台。
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Set<String> TYPES = Set.of("系统公告", "饲养知识");
    private static final Set<String> STATUS = Set.of("已发布", "未发布");

    private final NoticeMapper noticeMapper;
    private final SysUserMapper sysUserMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper, SysUserMapper sysUserMapper) {
        this.noticeMapper = noticeMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<NoticeVO> page(NoticeQueryDTO query) {
        Page<Notice> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        Page<Notice> result = noticeMapper.selectPage(page, buildWrapper(query, false));
        List<NoticeVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeVO add(NoticeSaveDTO dto) {
        checkSave(dto);
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setCreatorId(CurrentUserContext.getUserId());
        fillPublishTime(notice, null);
        noticeMapper.insert(notice);
        return toVO(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeVO update(Long id, NoticeSaveDTO dto) {
        checkSave(dto);
        Notice notice = getNotice(id);
        String oldStatus = notice.getPublishStatus();
        BeanUtils.copyProperties(dto, notice);
        fillPublishTime(notice, oldStatus);
        noticeMapper.updateById(notice);
        return toVO(notice);
    }

    @Override
    public void delete(Long id) {
        getNotice(id);
        noticeMapper.deleteById(id);
    }

    @Override
    public List<NoticeVO> publicList(NoticeQueryDTO query) {
        return noticeMapper.selectList(buildWrapper(query, true)).stream().map(this::toVO).toList();
    }

    @Override
    public NoticeVO publicDetail(Long id) {
        Notice notice = getNotice(id);
        if (!"已发布".equals(notice.getPublishStatus())) {
            throw new BusinessException("公告不存在或暂未发布");
        }
        return toVO(notice);
    }

    private LambdaQueryWrapper<Notice> buildWrapper(NoticeQueryDTO query, boolean onlyPublic) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .like(StringUtils.hasText(query.getTitle()), Notice::getTitle, query.getTitle())
                .eq(StringUtils.hasText(query.getType()), Notice::getType, query.getType())
                .eq(StringUtils.hasText(query.getPublishStatus()), Notice::getPublishStatus, query.getPublishStatus())
                .orderByDesc(Notice::getPublishTime)
                .orderByDesc(Notice::getCreateTime);
        if (onlyPublic) {
            wrapper.eq(Notice::getPublishStatus, "已发布");
        }
        return wrapper;
    }

    private void checkSave(NoticeSaveDTO dto) {
        if (!TYPES.contains(dto.getType())) {
            throw new BusinessException("公告类型不正确");
        }
        if (!STATUS.contains(dto.getPublishStatus())) {
            throw new BusinessException("发布状态不正确");
        }
    }

    private void fillPublishTime(Notice notice, String oldStatus) {
        if ("已发布".equals(notice.getPublishStatus()) && notice.getPublishTime() == null) {
            notice.setPublishTime(LocalDateTime.now());
        }
        if ("已发布".equals(notice.getPublishStatus()) && "未发布".equals(oldStatus)) {
            notice.setPublishTime(LocalDateTime.now());
        }
        if ("未发布".equals(notice.getPublishStatus())) {
            notice.setPublishTime(null);
        }
    }

    private Notice getNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    private NoticeVO toVO(Notice notice) {
        NoticeVO vo = new NoticeVO();
        BeanUtils.copyProperties(notice, vo);
        SysUser creator = notice.getCreatorId() == null ? null : sysUserMapper.selectById(notice.getCreatorId());
        vo.setCreatorName(creator == null ? null : creator.getRealName());
        return vo;
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
