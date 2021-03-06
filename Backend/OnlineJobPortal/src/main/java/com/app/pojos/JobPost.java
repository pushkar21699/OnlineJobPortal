package com.app.pojos;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="job_post_tbl")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobPost {
	

	@Id //primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) //for automatic generation of id
	private Integer id;
	
	@Column(name="created_date")
	private LocalDate createdDate;
	@Column(name="is_active")
	private boolean isActive;
	@Column(name="job_title")
	private String jobTitle;
	@Column(name="job_description")
	private String jobDescription;
	@Column(name="job_experience")
	private double jobExperience;
	//private String requiredCanidateProfile;
	//fk-->
	//jobType id
	//many posted jobs can have a one job_type
	@ManyToOne
	@JoinColumn(name="job_type_id",nullable = false)
	private JobType jobTypeId;

	//compnay_id
	//one company can post many jobs
	@ManyToOne
	//@JsonIgnore
	@JoinColumn(name="compony_id",nullable = false)
	private Company componyId;
	
	
	//job location id
	//many job post ---> one location
	@ManyToOne
	@JoinColumn(name="job_location_id",nullable = false)
	private JobLocation jobLocationId;
	
	public JobPost(LocalDate createdDate, String jobTitle, String jobDescription, double jobExperience,
			JobType jobType, Company company, JobLocation jobLocation) {
			this.createdDate=createdDate;
			this.isActive=true;
			this.jobTitle=jobTitle;
			this.jobDescription=jobDescription;
			this.jobExperience=jobExperience;
			this.jobTypeId=jobType;
			this.componyId=company;
			this.jobLocationId=jobLocation;
	}
}
