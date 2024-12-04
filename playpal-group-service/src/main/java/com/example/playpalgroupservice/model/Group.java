package com.example.playpalgroupservice.model;

import com.example.playpalgroupservice.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "group_table")
public class Group {

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_seq_gen")
    private Long groupID;


    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_description")
    private String groupDescription;

    @ElementCollection
    @CollectionTable(name = "group_user_ids", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private List<Long> userIds;

}
