package models;

/**
 * Member - Model class representing a library member.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class Member {

    private int    memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String joinedDate;
    private String status;     // "Active" | "Inactive"

    // ── Constructors ──────────────────────────────────────────────────────────

    public Member() {}

    public Member(int memberId, String name, String email,
                  String phone, String address,
                  String joinedDate, String status) {
        this.memberId   = memberId;
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.address    = address;
        this.joinedDate = joinedDate;
        this.status     = status;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getMemberId()                  { return memberId; }
    public void   setMemberId(int memberId)      { this.memberId = memberId; }

    public String getName()                      { return name; }
    public void   setName(String name)           { this.name = name; }

    public String getEmail()                     { return email; }
    public void   setEmail(String email)         { this.email = email; }

    public String getPhone()                     { return phone; }
    public void   setPhone(String phone)         { this.phone = phone; }

    public String getAddress()                   { return address; }
    public void   setAddress(String address)     { this.address = address; }

    public String getJoinedDate()                { return joinedDate; }
    public void   setJoinedDate(String date)     { this.joinedDate = date; }

    public String getStatus()                    { return status; }
    public void   setStatus(String status)       { this.status = status; }

    @Override
    public String toString() { return name + " (" + phone + ")"; }
}
