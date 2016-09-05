package org.openmrs.module.mohbilling.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohbilling.businesslogic.AdmissionUtil;
import org.openmrs.module.mohbilling.businesslogic.GlobalBillUtil;
import org.openmrs.module.mohbilling.model.Admission;
import org.openmrs.module.mohbilling.model.GlobalBill;
import org.openmrs.module.mohbilling.model.InsurancePolicy;
import org.openmrs.module.mohbilling.service.BillingService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class MohBillingAdmissionFormController extends
		ParameterizableViewController {

	protected final Log log = LogFactory.getLog(getClass());
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		ModelAndView mav = new ModelAndView();
		InsurancePolicy ip =null;	
		String discharge = request.getParameter("discharge");
		GlobalBill gb =null;
		log.info("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK "+discharge);
	if(discharge==null)
	if (request.getParameter("save") != null && request.getParameter("save").equals("true")) {
		
	     ip =Context.getService(BillingService.class).getInsurancePolicy(Integer.valueOf(request.getParameter("insurancePolicyId")));
	     
		
		Admission admission = new Admission();
		admission.setAdmissionDate(new Date());
		admission.setInsurancePolicy(ip);
		admission.setDischargingDate(new Date());
		admission.setIsAdmitted(true);
		admission.setCreator(Context.getAuthenticatedUser());
		admission.setCreatedDate(new Date());
	
		Admission savedAdmission = AdmissionUtil.savePatientAdmission(admission);	
		
		//create new Global bill
		gb =new GlobalBill();
		gb.setAdmission(savedAdmission);
		gb.setBillIdentifier(ip.getInsuranceCardNo()+savedAdmission.getAdmissionId());
		gb.setCreatedDate(new Date());
		gb.setCreator(Context.getAuthenticatedUser());
		
		
		gb =   GlobalBillUtil.saveGlobalBill(gb);
		mav.addObject("globalBill",gb);
		
	}
	if(request.getParameter("discharge")!=null){
		discharge = request.getParameter("discharge");
		gb = GlobalBillUtil.getGlobalBill(Integer.valueOf(request.getParameter("globalBillId")));
		if(request.getParameter("edit")!=null){
		 gb.setAdmission(gb.getAdmission());
		 gb.setBillIdentifier(gb.getBillIdentifier());
		 gb.setGlobalAmount(gb.getGlobalAmount());
		 gb.setCreator(gb.getCreator());
		 gb.setCreatedDate(gb.getCreatedDate());
		 gb.setClosingDate(new Date());
		 gb.setClosedBy(Context.getAuthenticatedUser());
		 gb.setClosed(true);
		 
		}
		gb =   GlobalBillUtil.saveGlobalBill(gb);
		mav.addObject("globalBill", gb);
	}
	
	
	 ip = Context.getService(BillingService.class).getInsurancePolicy(Integer.valueOf(request.getParameter("insurancePolicyId")));
		
	    mav.addObject("discharge", discharge);
		mav.addObject("insurancePolicy", ip);
		mav.setViewName(getViewName());
		return mav;
	}

}