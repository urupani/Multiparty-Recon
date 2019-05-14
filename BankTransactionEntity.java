package com.demo.MultipartyRecon.Entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BankTransactionEntity {
	
	@Column(name="id")
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="txn_id")
	private String transactionId;

	@Column(name="txn_amount")
	private Double transactionAmount;
	
	@Column(name="txn_date")
	private Date transactionDate;
	
	@Column(name="credit_flag")
	private String creditFlag; //true for credit 
	
	@Column(name="bal_after_txn")
	private Double balanceAfterTxn;
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="bank_code")
	private String bankCode;
	
	@Column(name="value_date")
	private Date valueDate;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String isCreditFlag() {
		return creditFlag;
	}

	public void setCreditFlag(String creditFlag) {
		this.creditFlag = creditFlag;
	}

	public Double getBalanceAfterTxn() {
		return balanceAfterTxn;
	}

	public void setBalanceAfterTxn(Double balanceAfterTxn) {
		this.balanceAfterTxn = balanceAfterTxn;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	
}

