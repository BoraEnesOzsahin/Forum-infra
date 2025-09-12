package com.ayrotek.forum.dto;

import java.time.Instant;
import java.util.List;
import com.ayrotek.forum.entity.Thread.VehicleType;

public class ThreadDto {
	private Long id;
	private String username;
	private VehicleType vehicleType;
	private String modelId;
	private String title;
	private Instant createdAt;
	private List<String> tags;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public VehicleType getVehicleType() { return vehicleType; }
	public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

	public String getModelId() { return modelId; }
	public void setModelId(String modelId) { this.modelId = modelId; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

	public List<String> getTags() { return tags; }
	public void setTags(List<String> tags) { this.tags = tags; }
}
