package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.OrganizationBean;

public interface OrganizationRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	Optional<OrganizationBean> findById(Integer id);
	List<OrganizationBean> findByName(String name);
	
	OrganizationBean saveAndFlush(OrganizationBean organization);
}