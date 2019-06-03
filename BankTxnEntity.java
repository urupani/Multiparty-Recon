package com.demo.MultipartyRecon.Entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="bank_transaction_entity")
public class BankTxnEntity {
	
	@Id
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	private double bal_after_txn;
	
	private String bank_code;
	
	private String credit_flag;
	
	private double txn_amount;
	
	private Date txn_date;
	
	private String txn_id;
	
	private Date value_date;
	
	private String partner;
	
	private String file_name;
	
	private String txn_type;

		public double getBal_after_txn() {
		return bal_after_txn;
	}

	public void setBal_after_txn(double bal_after_txn) {
		this.bal_after_txn = bal_after_txn;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getCredit_flag() {
		return credit_flag;
	}

	public void setCredit_flag(String credit_flag) {
		this.credit_flag = credit_flag;
	}
	

	public double getTxn_amount() {
		return txn_amount;
	}

	public void setTxn_amount(double txn_amount) {
		this.txn_amount = txn_amount;
	}

	public Date getTxn_date() {
		return txn_date;
	}

	public void setTxn_date(Date txn_date) {
		this.txn_date = txn_date;
	}

	public String getTxn_id() {
		return txn_id;
	}

	public void setTxn_id(String txn_id) {
		this.txn_id = txn_id;
	}

	public Date getValue_date() {
		return value_date;
	}

	public void setValue_date(Date value_date) {
		this.value_date = value_date;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getTxn_type() {
		return txn_type;
	}

	public void setTxn_type(String txn_type) {
		this.txn_type = txn_type;
	}
	
	
	public BankTxnEntity() {}
	

}
