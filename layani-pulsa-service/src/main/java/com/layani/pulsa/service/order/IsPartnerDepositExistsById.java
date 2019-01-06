package com.layani.pulsa.service.order;

import com.layani.pulsa.service.constant.ErrorConstant;
import org.slerp.core.CoreException;
import org.slerp.core.business.DefaultBusinessFunction;
import com.layani.pulsa.entity.PartnerDeposit;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.layani.pulsa.repository.PartnerDepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * @author kiditz
 * */
@Service
@KeyValidation("id")
@NumberValidation({"id"})
@NotBlankValidation({})
public class IsPartnerDepositExistsById extends DefaultBusinessFunction {
	@Autowired
	private PartnerDepositRepository partnerDepositRepository;

	@Override
	public Domain handle(Domain partnerDepositDomain) {
		PartnerDeposit partnerDeposit = partnerDepositRepository.findPartnerDepositById(partnerDepositDomain.getLong("id"));
		Domain result = new Domain();
		if (partnerDeposit != null){
			result.put("exists", Boolean.TRUE);
			result.put("partnerDeposit", partnerDeposit);
		}else{
			result.put("exists", Boolean.FALSE);
		}
		return result;
	}
}