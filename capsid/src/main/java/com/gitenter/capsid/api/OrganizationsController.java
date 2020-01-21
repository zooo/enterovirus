package com.gitenter.capsid.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.service.OrganizationManagerService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.UserBean;

@RestController
@RequestMapping(value="/api/organizations")
public class OrganizationsController {
	
	@Autowired UserService userService;
	@Autowired OrganizationService organizationService;
	@Autowired private OrganizationManagerService organizationManagerService;
	
	@PostMapping
	public EntityModel<OrganizationBean> createOrganization(
			@RequestBody @Valid OrganizationDTO organizationDTO,
			Authentication authentication) throws IOException {
		
		UserBean me = userService.getMe(authentication);
		OrganizationBean organizationBean = organizationManagerService.createOrganization(me, organizationDTO);
		
		return new EntityModel<>(organizationBean,
				linkTo(methodOn(OrganizationsController.class).createOrganization(organizationDTO, authentication)).withSelfRel(),
				linkTo(methodOn(OrganizationsController.class).getOrganization(organizationBean.getId())).withRel("organization"));
	}

	@GetMapping("/{organizationId}")
	public EntityModel<OrganizationBean> getOrganization(@PathVariable @Min(1) Integer organizationId) throws IOException {
		return new EntityModel<>(organizationService.getOrganization(organizationId),
				linkTo(methodOn(OrganizationsController.class).getOrganization(organizationId)).withSelfRel());
	}
	
	@DeleteMapping("/{organizationId}")
	public void deleteOrganization(
			@PathVariable @Min(1) Integer organizationId,
			@RequestParam(value="organization_name") String organizationName) throws IOException {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		/*
		 * TODO:
		 * Move this part of the logic to controller.
		 */
		if (!organization.getName().equals(organizationName)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, 
					"Organization name doesn't match!");
		}
		
		organizationManagerService.deleteOrganization(organization);
	}
}
