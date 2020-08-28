package com.qdm.cs.usermanagement.serviceimpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.repository.SkillsRepository;
import com.qdm.cs.usermanagement.service.SkillsService;

@Service
@Transactional
public class SkillsServiceImpl implements SkillsService{

	@Autowired
	SkillsRepository skillsRepository;
	
	@Override
	public Skills addSkillsList(Skills skills) {
		Skills skillById = skillsRepository.findBySkillId(skills.getSkillId());
		if (skillById != null) {
			return null;
		} else {
			return skillsRepository.save(skills);
		}
	}

	@Override
	public List<Skills> getSkillsList() {
		return skillsRepository.findAll();
	}

}
