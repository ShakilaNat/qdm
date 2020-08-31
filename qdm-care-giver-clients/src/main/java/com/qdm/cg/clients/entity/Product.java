package com.qdm.cg.clients.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_product")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long product_id;
	private long clientId;
	private String product_name;
	private String product_desc;
	private String product_code;
	private String review_description;
	private String review_title;
	private String rating_out_of_five;
	private String reviewed_on;
	private String current_status;
	private String current_status_date;
	private String product_image;
	private String product_price;
	private String offer;
	
}
