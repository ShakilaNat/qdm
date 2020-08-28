package com.qdm.cs.usermanagement.service;

import java.util.List;

import com.qdm.cs.usermanagement.entity.Skills;

public interface SkillsService {

	Skills addSkillsList(Skills skills);

	List<Skills> getSkillsList();

}
