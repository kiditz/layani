package com.layani.pulsa.service.deposit;

import com.layani.pulsa.entity.Deposit;
import com.layani.pulsa.entity.DepositLog;
import com.layani.pulsa.repository.DepositLogRepository;
import com.layani.pulsa.repository.DepositRepository;
import com.layani.pulsa.service.constant.ErrorConstant;
import com.layani.pulsa.service.constant.ServiceConstant;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessTransaction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
/**
 * @apiNote EditDeposit always use plus(+) operator to add or subtract the
 * deposit balance amount. so please use minus(-) value in the amount if you want to cut the balance
 *
 * @author kiditz
 * */
@Service
@Transactional
@KeyValidation({"outletId", "amount"})
@NumberValidation({"outletId"})
public class EditDeposit extends DefaultBusinessTransaction {

	@Autowired
	private DepositRepository depositRepository;
	@Autowired
	private DepositLogRepository depositLogRepository;

	@Override
	public void prepare(Domain depositDomain) {
		Long outletId = depositDomain.getLong("outletId");
		Double amount = depositDomain.getDouble("amount");
		Deposit deposit = depositRepository.findByOutletId(outletId);
		if(deposit == null){
			throw new CoreException(ErrorConstant.DEPOSIT_NOT_FOUND);
		}

		depositDomain.put("before", deposit.getAmount());
		// Update Amount
        Double calculateAmount = deposit.getAmount().doubleValue() + amount;
        if(calculateAmount < 0){
            throw new CoreException(ErrorConstant.BALANCE_NOT_ENOUGH);
        }
        deposit.setAmount(BigDecimal.valueOf(calculateAmount));
        depositDomain.put("after", deposit.getAmount());
        depositDomain.put("numOfTransaction", amount);

		deposit.setUpdateAt(new Date());
		deposit.setOutletId(outletId);
        depositDomain.put("deposit", new Domain(deposit));

	}

	@Override
	public Domain handle(Domain depositDomain) {
		super.handle(depositDomain);
		Double amount = depositDomain.getDouble("amount");
		try {
			Deposit deposit = depositDomain.getDomain("deposit").convertTo(Deposit.class);
			deposit = this.depositRepository.save(deposit);
			DepositLog depositLog = new DepositLog();
			depositLog.setDepositId(deposit);
			depositLog.setBalanceAmount(BigDecimal.valueOf(amount));
			depositLog.setCreatedAt(new Date());
			if(amount < 0){
				depositLog.setRefId(ServiceConstant.REFID_TRX);
				depositLog.setRemark(ServiceConstant.CUT_DEPOSIT);
			}else{
				depositLog.setRefId(ServiceConstant.REFID_DEPOSIT);
				depositLog.setRemark(ServiceConstant.ADD_DEPOSIT);
			}
			depositLog.setOutletId(deposit.getOutletId());
			this.depositLogRepository.save(depositLog);
			Domain result = new Domain(deposit);
			result.put("before", depositDomain.getDouble("before"));
            result.put("after", depositDomain.getDouble("after"));
            result.put("numOfTransaction", depositDomain.getDouble("numOfTransaction"));
			return result;
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}
}