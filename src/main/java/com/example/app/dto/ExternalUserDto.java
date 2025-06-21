package com.example.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar um User da API JSONPlaceholder
 */
public class ExternalUserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("website")
    private String website;

    @JsonProperty("address")
    private AddressDto address;

    @JsonProperty("company")
    private CompanyDto company;

    // Construtores
    public ExternalUserDto() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }

    // Classes internas para Address e Company
    public static class AddressDto {
        @JsonProperty("street")
        private String street;

        @JsonProperty("suite")
        private String suite;

        @JsonProperty("city")
        private String city;

        @JsonProperty("zipcode")
        private String zipcode;

        @JsonProperty("geo")
        private GeoDto geo;

        // Getters e Setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getSuite() {
            return suite;
        }

        public void setSuite(String suite) {
            this.suite = suite;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public GeoDto getGeo() {
            return geo;
        }

        public void setGeo(GeoDto geo) {
            this.geo = geo;
        }

        public static class GeoDto {
            @JsonProperty("lat")
            private String lat;

            @JsonProperty("lng")
            private String lng;

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }
        }
    }

    public static class CompanyDto {
        @JsonProperty("name")
        private String name;

        @JsonProperty("catchPhrase")
        private String catchPhrase;

        @JsonProperty("bs")
        private String bs;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCatchPhrase() {
            return catchPhrase;
        }

        public void setCatchPhrase(String catchPhrase) {
            this.catchPhrase = catchPhrase;
        }

        public String getBs() {
            return bs;
        }

        public void setBs(String bs) {
            this.bs = bs;
        }
    }

    @Override
    public String toString() {
        return "ExternalUserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

