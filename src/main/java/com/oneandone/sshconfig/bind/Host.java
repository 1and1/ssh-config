/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oneandone.sshconfig.bind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A single host entry.
 * @author Stephan Fuhrmann
 */
@EqualsAndHashCode(of = "fqdn")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Host {

    @Getter @Setter @NotNull
    private UUID id;
    
    @Getter @Setter
    private String fqdn;
    
    @Getter @Setter @NotNull
    private String name;
    
    @Getter @Setter @NotNull
    private String[] ips;
    
    @Getter @Setter @NotNull
    private Date createdAt;
    
    @Getter @Setter @NotNull
    private Date updatedAt;

    @Getter @Setter @NotNull
    private String sshServerVersion;

    @Getter @Setter @NotNull
    private Boolean enabled;

    public Host() {
        id = UUID.randomUUID();
        createdAt = new Date();
        updatedAt = createdAt;
    }

    public Host(UUID id, String fqdn, String name, Date createdAt, Date updatedAt) {
        this.id = id;
        this.fqdn = fqdn;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
