package com.project.dto;

import java.io.Serializable;

public record LoginMember(Long id, String loginId, String name, String role) implements Serializable {
}
