package com.app.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.CityRepository;
import com.app.dao.JobPostRepository;
import com.app.dao.JobPostRequiredSkillSetsRepository;
import com.app.dao.JobSeekerAppliedJobsRepository;
import com.app.dao.JobSeekerRepository;
import com.app.dao.JobSeekerSkillSetsRepository;
import com.app.dao.LoginRepository;
import com.app.dao.RoleRepository;
import com.app.dao.SkillSetRepository;
import com.app.dto.Education;
import com.app.dto.Experience;
import com.app.dto.JobPostResponse;
import com.app.dto.JobSeekerMultipleSkills;
import com.app.dto.JobSeekerRegistration;
import com.app.dto.JobSeekerSkill;
import com.app.dto.JobSeekerUpdateDTO;
import com.app.dto.SearchDTO;
import com.app.pojos.City;
import com.app.pojos.JobPost;
import com.app.pojos.JobPostRequiredSkillSets;
import com.app.pojos.JobSeeker;
import com.app.pojos.JobSeekerAppliedJobs;
import com.app.pojos.JobSeekerEducation;
import com.app.pojos.JobSeekerExperience;
import com.app.pojos.JobSeekerSkillSets;
import com.app.pojos.LoginDetails;
import com.app.pojos.Role;
import com.app.pojos.SkillSets;

@Service // tells spring container that it is bean , craete it's dependecy(instance) at
			// app deploy time(i.e when we start the app at that time)
@Transactional
public class JobSeekerServiceImpl implements IJobSeekerService {

	// dao injection dependency
	@Autowired
	private JobSeekerRepository jobSeekerRepo;
	@Autowired
	private LoginRepository loginRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private SkillSetRepository skillRepo;
	@Autowired
	private JobSeekerSkillSetsRepository jobSeekerSkillRepo;
	@Autowired
	private CityRepository cityRepo;
	@Autowired
	private JobPostRepository jobPostRepo;
	@Autowired
	private JobSeekerAppliedJobsRepository jobSeekerAppliedRepo;
	@Autowired
	private JobPostRequiredSkillSetsRepository jobPostRequiredSkillRepo;
	@Autowired
	private EmailSenderService emailService;

	// add a method to save data into jobSeeker table
	@Override
	public String JobSeekerRegistration(JobSeekerRegistration register) {
		// check jobSeeker is already exist or not , by using emailId
		if (loginRepo.findByEmailId(register.getEmailId()) != null)
			throw new RuntimeException("JobSeeker already exist");
		System.out.println("in JobSeekerServiceImpl  ");
		// find role by roleid of jobseeker:2
		Role role = roleRepo.findById(2).orElseThrow(() -> new RuntimeException("Role Not Found!!"));
		// if control goes here means jobSeekr not exist(i.e.new job seeker) and we can
		// store jobSeeker details in database
		LoginDetails jobSeekerLogin = new LoginDetails(register.getEmailId(), register.getPassword(),
				register.getPhoneNumber(), register.getUsername(), role);

		// save login details in login table
		LoginDetails savedJobSeekerLogin = loginRepo.save(jobSeekerLogin);
		// City city = cityRepo.findByCityName(register.getCity());
		City city = cityRepo.findById(register.getCity()).orElseThrow(() -> new RuntimeException("City Not Found!!"));
		// City city = cityRepo.findById(register.getCity());
		// int loginId = savedJobSeekerLogin.getLoginId();
		JobSeeker jobSeeker = new JobSeeker(register.getFirstName(), register.getLastName(), register.getGender(),
				register.getDob(), register.getHouseNumber(), register.getStreet(), register.getPincode(), city,
				savedJobSeekerLogin);

		// and save other registration details in JobSeeker Table
		jobSeeker = jobSeekerRepo.save(jobSeeker);
		jobSeeker.setCountry("India");
		// after successful registration send email to user
		if (jobSeeker.getLoginId().getEmailId().equals(register.getEmailId()))
			emailService.sendMailToUser(jobSeekerLogin.getEmailId(),
					register.getFirstName() + " Welcome to Online Job Portal", "Find Your dream Job Now");
		return "Registration SuccessFully!!!!!!";
	}

	//update jobSeeker info
	public String updateJobSeekerInfo(JobSeekerUpdateDTO jobseeker, int id) {
		JobSeeker updateJobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		updateJobseeker.setFirstName(jobseeker.getFirstName());
		updateJobseeker.setLastName(jobseeker.getLastName());
		updateJobseeker.setGender(jobseeker.getGender());
		updateJobseeker.setDob(jobseeker.getDob());
		updateJobseeker.setStreet(jobseeker.getStreet());
		updateJobseeker.setHouseNumber(jobseeker.getHouseNumber());
		updateJobseeker.setPincode(jobseeker.getPincode());
		updateJobseeker.setCity(cityRepo.findByCityName(jobseeker.getCity()));
		return "update successfully";
	}

	// add a method to delete the job seeker
	@Override
	public void deleteJobSeeker(int id) {
		System.out.println("in jobseeker delete service layer");
		// serivce layer invoke dao's method
		// Deletes the entity with the given id.
		// if id found it delete from database and if id is null it simply throws the
		// exception
		jobSeekerRepo.deleteById(id);
	}

	@Override
	public List<JobSeeker> getAllJobSeeker() {
		// it will give list of all jobseeker + thier education details (education
		// details is eagerly fetching so it come up with jobseeker details)
		List<JobSeeker> list = jobSeekerRepo.findAll(); // due to eager policy , we get education details
		// experience details are lazily fetch , so getExperienceDetails().size() with
		// this function we get the experice details data
		list.forEach(jobseeker -> jobseeker.getExperienceDetails().size()); // due to size() this line experience
																			// details are get bcoz it is lazy type
		return list;
	}

	//get jobSeeker Education
	@Override
	public List<JobSeekerEducation> getEducation(int id) {
		System.out.println("In jobseeker service :getEducation ");
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		return jobseeker.getAllEducation();
	}

	//get JobSeeker details
	@Override
	public JobSeeker getJobSeeker(int id) {
		System.out.println("In job seeker get by id");
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		jobseeker.getExperienceDetails().size(); // due to this we got experience details
		return jobseeker;
	}

	//methdo to add / update education
	@Override
	public void addOrUpdateEducation(Education e, int id) {
		System.out.println("in jobseeker adddorupdate education " + id + " education: " + e);
		// 1. find jobseeker by it's id
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		// 2. if jobseeker found set the education details
		JobSeekerEducation education = new JobSeekerEducation(e.getEducationType(), e.getUniversityName(),
				e.getInstituteName(), e.getPassingYear(), e.getPercentage());
		jobseeker.getEducationDetails().add(education);
	}

	// add a method to add/update expr details
	@Override
	public void addOrUpdateExperience(Experience e, int id) {
		// 1. find jobseeker id
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		// 2. if jobseeker found , create JobSeekerExperience and add into its exp
		// details
		JobSeekerExperience experience = new JobSeekerExperience(e.getDesignation(), e.getCompany(),
				e.isCurrentCompany(), e.getCurrentSalary(), e.getJobDescription(), e.getStartDate(), e.getEndDate());
		jobseeker.getExperienceDetails().add(experience);
	}

	//get experience details
	@Override
	public List<JobSeekerExperience> getExperience(int id) {
		System.out.println("In jobseeker service :getExperience ");
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		return jobseeker.getAllExperince();
	}

	// method to add job seeker skills
	@Override
	public void addSkill(JobSeekerSkill skill, int id) {
		System.out.println("In jobseeker service :addSkill ");
		// 1. find jobseeker
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		// 2. if jobseeker present
		// find skillset id by skilltype
		SkillSets skillset = skillRepo.findBySkillType(skill.getSkillType());
		System.out.println(skillset);
		// 3. save data into JobSeekerSkillSets table
		JobSeekerSkillSets jobSeekerSkillSets = new JobSeekerSkillSets(jobseeker, skillset);
		jobSeekerSkillRepo.save(jobSeekerSkillSets);
	}

	//add multiple skills
	@Override
	public String addMultipleSkills(JobSeekerMultipleSkills skills, int id) {
		// 1. find jobseeker
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		SkillSets skillset = null;
		JobSeekerSkillSets jobSeekerSkillSets = null;
		// iterate over the skills
		for (Integer i : skills.getSkills()) {
			// find skillname by id add into jobseeker skills
			// if jobseekerid and skillset Id already availble , it will not add the
			// skillsets again (Drawback: But to check that it need to scan the table)
			if (!(skillRepo.existsById(i) && jobSeekerRepo.existsById(jobseeker.getJobSeekerId()))) {
				skillset = skillRepo.findById(i).orElseThrow(() -> new RuntimeException("Skillset Not Found!!"));
				jobSeekerSkillSets = new JobSeekerSkillSets(jobseeker, skillset);
				jobSeekerSkillRepo.save(jobSeekerSkillSets);
			}
		}
		return "JobSeeker mutiple skillsets added successfully";
	}

	//get jobSeeker Skills
	@Override
	public List<JobSeekerSkillSets> getAllJobSeekerSkills(int id) {
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		return jobSeekerSkillRepo.findByJobSeekerId(jobseeker);

	}

	// method to apply job
	@Override
	public String applyToJob(int jobSeekerId, int jobPostId) {
		// check job seeker present or not
		JobSeeker jobseeker = jobSeekerRepo.findById(jobSeekerId)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		// check jobPostId
		JobPost jobpost = jobPostRepo.findById(jobPostId)
				.orElseThrow(() -> new RuntimeException("Job Post Not Found!!"));
		jobseeker.getExperienceDetails().size(); // due to this we got experience details

		// save jobPost
		JobSeekerAppliedJobs jobApplication = new JobSeekerAppliedJobs(jobseeker, jobpost, LocalDate.now());
		jobSeekerAppliedRepo.save(jobApplication);
		return "Job Post apply successfully";

	}

	//get all jobs
	@Override
	public List<JobPostResponse> getAllJobs(int id) {
		// check job seeker present or not
		JobSeeker jobseeker = jobSeekerRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("jobseeker Not Found!!"));
		jobseeker.getExperienceDetails().size();
		List<JobSeekerAppliedJobs> jobList = jobSeekerAppliedRepo.findByJobSeekerId(jobseeker);
		List<JobPostResponse> jobPostList = new ArrayList<JobPostResponse>();
		for (JobSeekerAppliedJobs j : jobList) {
			jobPostList.add(new JobPostResponse(j.getApplyDate(), j.getJobPost().isActive(),
					j.getJobPost().getJobTitle(), j.getJobPost().getJobTypeId(), j.getJobPost().getJobLocationId(),
					j.getJobPost().getComponyId()));
		}
		return jobPostList;
	}

	//search by skill
	@Override
	public List<JobPostRequiredSkillSets> searchBySkillId(int skillId) {
		System.out.println("in search by skill name: ");
		return jobPostRequiredSkillRepo.findBySkillName(skillId);
	}
	
	//search by city
	@Override
	public List<JobPost> searchByCityId(int cityId) {
		System.out.println("Search by city  : ");
		return jobPostRepo.findByJobLocation(cityId);
	}

	// to get jobList by multiple skillsets
	@Override
	public List<JobPost> getJobList(ArrayList<Integer> skills) {
		System.out.println("Search by  skills : ");
		return jobPostRequiredSkillRepo.findBySkillSetIDIn(skills);
	}

	// search by city + skillset
	@Override
	public List<JobPost> getJobListByCityAndSkills(ArrayList<Integer> skills, int cityId) {
		System.out.println("Search by city and skills : ");
		List<JobPost> jobList = new ArrayList<JobPost>();
		// List sort by SkillSet
		List<JobPost> jobBySkills = jobPostRequiredSkillRepo.findBySkillSetIDIn(skills);
		// now sort list by city from jobBySkills
		for (JobPost j : jobBySkills) {
			if (j.getJobLocationId().getCity().getId() == cityId)
				jobList.add(j);
		}
		return jobList;
	}

	//search by jobType
	@Override
	public List<JobPost> searchByJobType(int jobType) {
		return jobPostRepo.findByJobType(jobType);
	}

	//get latest jobs
	@Override
	public List<JobPost> getLatestJobs() {
		return jobPostRepo.getLatestJobs(10);
	}

	//find city
	@Override
	public City findCity(int cityId) {
		return cityRepo.findById(cityId).orElseThrow(() -> new RuntimeException("City Not Found!!"));
	}

	//search jobs by skills and city
	@Override
	public List<JobPost> searching(SearchDTO search) {
		List<JobPost> jobs=null;
		if (search.getSkills().size() != 0 && search.getCity() != null)
			jobs=getJobListByCityAndSkills(search.getSkills(), search.getCity());
		else if(search.getCity() != null)
			jobs=searchByCityId(search.getCity());
		else
			jobs=getJobList(search.getSkills());		
	return jobs;	
	}

}
