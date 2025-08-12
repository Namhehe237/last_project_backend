package com.example.demo.examOnline.domain;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SYSTEM_SETTINGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting {
    @Id
    @Column(name = "setting_key", length = 255)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;
}
