package no.micro.livesearch_service2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunescapeCharDTO {
    private String runescapeName; // RuneScape username
    private int coxKC; // Chambers of Xeric KC
    private int tobKC; // Theatre of Blood KC
    private int toaKC; // Tombs of Amascut KC
}
