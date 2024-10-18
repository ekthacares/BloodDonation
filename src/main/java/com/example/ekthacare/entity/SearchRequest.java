package com.example.ekthacare.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
	public class SearchRequest {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long userId;
	    private String bloodgroup;
	    private String city;
	    private String state;
	    private String timestamp;
	    
	     


		public SearchRequest() {
			// TODO Auto-generated constructor stub
		}




		public Long getId() {
			return id;
		}




		public void setId(Long id) {
			this.id = id;
		}




		public Long getUserId() {
			return userId;
		}




		public void setUserId(Long userId) {
			this.userId = userId;
		}




		public String getBloodgroup() {
			return bloodgroup;
		}




		public void setBloodgroup(String bloodgroup) {
			this.bloodgroup = bloodgroup;
		}




		public String getCity() {
			return city;
		}




		public void setCity(String city) {
			this.city = city;
		}




		public String getState() {
			return state;
		}




		public void setState(String state) {
			this.state = state;
		}


		 @PrePersist
		    @PreUpdate
		    public void formatTimestamp() {
		        if (this.timestamp == null) {
		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss");
		            this.timestamp = LocalDateTime.now().withNano(0).format(formatter);
		        }
		    }


		 public String getTimestamp() {
		        return timestamp;
		    }

	
		 public void setTimestamp(LocalDateTime timestamp) {
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss");
		        this.timestamp = timestamp.withNano(0).format(formatter);
		    }



		public SearchRequest(Long id, Long userId, String bloodgroup, String city, String state,
				String timestamp) {
			super();
			this.id = id;
			this.userId = userId;
			this.bloodgroup = bloodgroup;
			this.city = city;
			this.state = state;
			this.timestamp = timestamp;
		}




		@Override
		public String toString() {
			return "SearchRequest [id=" + id + ", userId=" + userId + ", bloodgroup=" + bloodgroup + ", city=" + city
					+ ", state=" + state + ", timestamp=" + timestamp + "]";
		}


		
		

		
	    
	    
	     
	    

}
