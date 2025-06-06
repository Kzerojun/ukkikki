package com.dancing_orangutan.ukkikki.proposal.infrastructure.proposal;

import com.dancing_orangutan.ukkikki.proposal.constant.ProposalStatus;
import com.dancing_orangutan.ukkikki.proposal.domain.proposal.ProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaProposalRepository extends JpaRepository<ProposalEntity,Integer> {

    Optional<ProposalEntity> findById(Integer proposalId);

    List<ProposalEntity> findByCompany_CompanyId(Integer companyId);

    Optional<ProposalEntity> findByProposalIdAndCompany_CompanyId(Integer proposalId, Integer companyId);

    Optional<ProposalEntity> findByProposalId(Integer proposalId);

    Optional<ProposalEntity> findByProposalIdAndTravelPlan_TravelPlanIdAndProposalStatus(
            Integer proposalId, Integer travelPlanId, ProposalStatus proposalStatus
    );

    Optional<ProposalEntity> findByProposalIdAndTravelPlan_TravelPlanId(Integer proposalId, Integer travelPlanId);

    List<ProposalEntity> findByTravelPlan_TravelPlanId(Integer travelPlanId);

    List<ProposalEntity> findByCompany_CompanyIdAndProposalStatus(Integer companyId, ProposalStatus proposalStatus);
}
