/*
 * Copyright 2018 1&1 Internet SE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
