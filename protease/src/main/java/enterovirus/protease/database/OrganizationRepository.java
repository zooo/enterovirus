package enterovirus.protease.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.OrganizationBean;

public interface OrganizationRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	public Optional<OrganizationBean> findById(Integer id);
	public List<OrganizationBean> findByName(String name);
	public OrganizationBean saveAndFlush(OrganizationBean organization);
}
