package com.qdm.cs.usermanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareProviderList {

	long id;
	String name;
	List<LabelValuePair> category;
	
}
