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
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.oneandone.sshconfig.validation.IpAddress;
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

    /** The unique ID of the host. */
    @Getter @Setter @NotNull
    private UUID id;

    /** Fully qualified domain name of the host. */
    @Getter @Setter @Size(min = 1) @IpAddress
    private String fqdn;

    /** The human readable name of the host. */
    @Getter @Setter @NotNull @Size(min = 1)
    private String name;

    /** The IP addresses of the host. */
    @Getter @Setter @NotNull @Size(min = 1) @IpAddress
    private String[] ips;

    /** The date of creation of this hosts entry. */
    @Getter @Setter @NotNull @PastOrPresent
    private Date createdAt;

    /** The date of last update of this hosts entry. */
    @Getter @Setter @NotNull @PastOrPresent
    private Date updatedAt;

    /** The ssh version this host responded with. */
    @Getter @Setter @Size(min = 1)
    private String sshServerVersion;

    /** Whether this host is enabled or not. */
    @Getter @Setter
    private Boolean enabled;

    /** The user to use for this host. */
    @Getter @Setter @Pattern(regexp = "[^ ]{1,}")
    private String user;

    /** Constructs a new empty host entry. */
    public Host() {
        id = UUID.randomUUID();
        createdAt = new Date();
        updatedAt = createdAt;
        enabled = true;
    }

    /**
    /** Constructs a new host entry.
     * @param inId the identifier to use.
     * @param inFqdn the fully qualified domain name.
     * @param inName the human readable name.
     * @param inCreatedAt the creation date.
     * @param inUpdatedAt the date of last update.
     * @param inUser the user to use.
     */
    public Host(final UUID inId,
            final String inFqdn,
            final String inName,
            final Date inCreatedAt,
            final Date inUpdatedAt,
            final String inUser) {
        this.id = Objects.requireNonNull(inId);
        this.fqdn = Objects.requireNonNull(inFqdn);
        this.name = Objects.requireNonNull(inName);
        this.createdAt = Objects.requireNonNull(inCreatedAt);
        this.updatedAt = Objects.requireNonNull(inUpdatedAt);
        this.user = inUser;
    }
}
