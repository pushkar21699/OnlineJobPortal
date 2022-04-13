package com.app.dto;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchDTO {
	private ArrayList<Integer> skills;	
	private Integer city;
}
