package com.qdm.cs.usermanagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.response.ResponseInfo;
import com.qdm.cs.usermanagement.response.ResponseType;
import com.qdm.cs.usermanagement.service.CareGiverService;
import com.qdm.cs.usermanagement.service.CategoryService;

@RestController
@RequestMapping(value = { "/category" })
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class CategoryController {

	@Autowired
	CareGiverService careGiverService;

	@Autowired
	CategoryService categoryService;

	@PostMapping(value = "/addCategoryList", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> addCategoryList(@RequestBody Category category) {
		ResponseEntity response = null;
		try {
			Category categoryData = categoryService.addCategoryList(category);
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/getCategoryList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCategoryList() {
		ResponseEntity response = null;
		try {
			List<Category> categoryList = categoryService.getCategoryList();
			List<Object> list=new ArrayList<Object>();
			for (Category category : categoryList) {
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("label", category.getCategoryName());
				map.put("value",category.getCategoryId());
				list.add(map);
			}
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", list), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}
}
