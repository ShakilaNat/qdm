package com.qdm.cs.usermanagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.response.ResponseInfo;
import com.qdm.cs.usermanagement.response.ResponseType;
import com.qdm.cs.usermanagement.service.SkillsService;

@RestController
@RequestMapping(value = { "/skills" })
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class SkillsController {

	@Autowired
	SkillsService skillsService;

	@PostMapping(value = "/addSkillsList", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> addSkillsList(@RequestBody Skills skills) {
		ResponseEntity response = null;
		try {
			Skills skillsData = skillsService.addSkillsList(skills);
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/getSkillsList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCategoryList() {
		ResponseEntity response = null;
		try {
			List<Skills> skillsList = skillsService.getSkillsList();
			List<Object> skillList = new ArrayList<Object>();
			for (Skills skills : skillsList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("label", skills.getSkillName());
				map.put("value", skills.getSkillId());
				skillList.add(map);
			}
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", skillList), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}
}
