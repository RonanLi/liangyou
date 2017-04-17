package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the attestation_type database table.
 * 
 */
@Entity
@Table(name="attestation_type")
public class AttestationType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="type_id")
	private int typeId;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private int jifen;

	private String name;

	private int sort;

	private String remark;

	private int status;

	private String summary;

	//bi-directional many-to-one association to Attestation
	@OneToMany(mappedBy="attestationType")
	private List<Attestation> attestations;

	public AttestationType() {
	}
	public AttestationType(int typeId) {
		super();
		this.typeId = typeId;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getAddip() {
		return this.addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public int getJifen() {
		return this.jifen;
	}

	public void setJifen(int jifen) {
		this.jifen = jifen;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<Attestation> getAttestations() {
		return this.attestations;
	}

	public void setAttestations(List<Attestation> attestations) {
		this.attestations = attestations;
	}

	public Attestation addAttestation(Attestation attestation) {
		getAttestations().add(attestation);
		attestation.setAttestationType(this);

		return attestation;
	}

	public Attestation removeAttestation(Attestation attestation) {
		getAttestations().remove(attestation);
		attestation.setAttestationType(null);

		return attestation;
	}

}