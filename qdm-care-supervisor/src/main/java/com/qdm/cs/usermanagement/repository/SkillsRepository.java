package com.qdm.cs.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.Skills;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Integer>{

	Skills findBySkillId(int skillId);

}
