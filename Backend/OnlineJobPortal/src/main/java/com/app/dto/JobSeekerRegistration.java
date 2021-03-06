package com.app.dto;

import java.time.LocalDate;

import com.app.pojos.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//to receive registration details from jobseeker and transfer over the other layers
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobSeekerRegistration {
	
	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private String phoneNumber;
	private String username;
	private Gender gender;
	private LocalDate dob;
	private String street;
	private String houseNumber;
	private int pincode;
	private int city;
}
