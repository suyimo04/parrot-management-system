package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.common.ResultCode;
import com.parrot.dto.SpeciesQueryDTO;
import com.parrot.dto.SpeciesSaveDTO;
import com.parrot.service.SpeciesService;
import com.parrot.vo.SpeciesVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台品种管理接口。
 */
@RestController
@RequestMapping("/api/admin/species")
public class SpeciesController {

    private final SpeciesService speciesService;

    public SpeciesController(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<SpeciesVO>> page(SpeciesQueryDTO query) {
        return ApiResult.success(speciesService.page(query));
    }

    @GetMapping("/list")
    public ApiResult<List<SpeciesVO>> listEnabled() {
        return ApiResult.success(speciesService.listEnabled());
    }

    @PostMapping
    public ApiResult<SpeciesVO> add(@Valid @RequestBody SpeciesSaveDTO dto) {
        checkAdmin();
        return ApiResult.success("新增品种成功", speciesService.add(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<SpeciesVO> update(@PathVariable Long id, @Valid @RequestBody SpeciesSaveDTO dto) {
        checkAdmin();
        return ApiResult.success("修改品种成功", speciesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        checkAdmin();
        speciesService.delete(id);
        return ApiResult.success("品种已删除或禁用", null);
    }

    private void checkAdmin() {
        if (CurrentUserContext.get() == null || !"ADMIN".equals(CurrentUserContext.get().getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "品种编辑功能仅管理员可用");
        }
    }
}
