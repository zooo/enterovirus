package enterovirus.capsid.web;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.springframework.util.AntPathMatcher;

import enterovirus.capsid.database.*;
import enterovirus.capsid.domain.*;

@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired private DocumentRepository documentRepository;
	@Autowired private OrganizationRepository organizationRepository;	
	@Autowired private MemberRepository memberRepository;
	@Autowired private NewMemberRepository newMemberRepository;

	/**
	 * List members
	 * 
	 * @param
	 * @return
	 * 
	 * TODO: refer to https://spring.io/guides/gs/rest-service-cors/ for
	 * further setups of Cross Origin Requests (CORS).
	 */
	@CrossOrigin
	@RequestMapping(value="/members", method=RequestMethod.GET)
	public Iterable<MemberBean> listMembers() {
		
		Iterable<MemberBean> members = memberRepository.findAll();
		return members;
	}
	
	/*
	 * test successful by feeding 
	 * {"id":4, "username":"ddd", "password":"ddd","displayName":"Ann Author","email":"ann@ann.com"}
	 * 
	 * Need a NewMemberBean rather than just using MemberBean because
	 * (1) with explicit "password" (without @JsonIgnore)
	 * and (2) without the organization array (Jackson has problem to handle that with
	 * error code related to @JsonManageredReference and @JsonBackReference,
	 * and we don't use it no matter what.
	 * 
	 * Currently cannot test if the "id" need to be setup explicitly, 
	 * otherwise key conflict. Need to check more carefully,
	 * because there were three members inserted using SQL "INSERT"
	 * which doesn't not change PostgreSQL's SEQUENCE. Old notes:
	 * 
	 * @GeneratedValue for automatically generate primary keys.
	 * PostgreSQL has some problem with Hibernate for automatic primary key generation. Basically only strategy=GenerationType.IDENTITY works, but it has performance issues (compare to SEQUENCE) -- not crucial for us.
	 * If our dummy data is made by INSERT using specific primary key, then it doesn't change the PostgreSQL's SEQUENCE so if later we insert without primary key (or let Hibernate to insert) that will cause ID conflict issues.
	 * 
	 */
	@CrossOrigin
	@RequestMapping(value="/members", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NewMemberBean> addMember(@RequestBody NewMemberBean member) {
		System.out.println(member);
		System.out.println(member.getUsername());
		System.out.println(member.getPassword());
		newMemberRepository.saveAndFlush(member);
		return new ResponseEntity<NewMemberBean>(member, HttpStatus.OK);
	}
	
	/**
	 * List member information
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/members/{username}", method=RequestMethod.GET)
	public MemberBean getMemberInformation(
			@PathVariable String username) {
		
		MemberBean member = memberRepository.findByUsername(username).get(0);
		return member;
	}
	
	/**
	 * List member repositories
	 * <p>
	 * List public repositories the specified member is involved.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/members/{username}/repositories", method=RequestMethod.GET)
	public DocumentBean listMemberRepositories(
			@PathVariable String username) {
		return null;
	}

	/**
	 * List organization information
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}", method=RequestMethod.GET)
	public OrganizationBean getOrganizationInformation(
			@PathVariable String organizationName) {
		
		OrganizationBean organization = organizationRepository.findByName(organizationName).get(0);
		return organization;
	}
	
	/**
	 * List organization repositories
	 * <p>
	 * List public repositories for the specified organization.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}/repositories", method=RequestMethod.GET)
	public DocumentBean listOrganizationRepositories(
			@PathVariable String orginizationName) {
		return null;
	}
	
	/**
	 * Get repository information
	 * <p>
	 * Since design control is most for big enterprise projects,
	 * and needs discussion between multiple people, a repository
	 * is always belong to an organization.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}/repositories/{repositoryName}", method=RequestMethod.GET)
	public DocumentBean getRepositoryInformation(
			@PathVariable String orginizationName,
			@PathVariable String repositoryName) {
		return null;
	}

	/**
	 * Get directory information (in branch)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}/repositories/{repositoryName}/branches/{branchName}/directories/**", method=RequestMethod.GET)
	public DocumentBean getDirectoryInformationInBranch(
			@PathVariable String organizationName,
			@PathVariable String repositoryName,
			@PathVariable String branchName,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String directoryPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return null;
	}
	
	/**
	 * Get directory information (in commit)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}/repositories/{repositoryName}/commits/{commitId}/directories/**", method=RequestMethod.GET)
	public DocumentBean getDirectoryInformationInCommit(
			@PathVariable String organizationName,
			@PathVariable String repositoryName,
			@PathVariable String commitId,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String directoryPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return null;
	}
	
	/**
	 * Get text file content (in branch)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}/repositories/{repositoryName}/branches/{branchName}/files/**", method=RequestMethod.GET)
	public DocumentBean getDocumentContentInBranch(
			@PathVariable String organizationName,
			@PathVariable String repositoryName,
			@PathVariable String branchName,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String filePath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		DocumentBean document = documentRepository.findDocument(organizationName, repositoryName, branchName, filePath);
		return document;
	}
	
	/**
	 * Get text file content (in commit)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organizationName}/repositories/{repositoryName}/commits/{commitId}/files/**", method=RequestMethod.GET)
	public DocumentBean getDocumentContentInCommit(
			@PathVariable String organizationName,
			@PathVariable String repositoryName,
			@PathVariable String commitId,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String filePath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return null;
	}
}