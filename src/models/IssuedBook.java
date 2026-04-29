package models;

/**
 * IssuedBook - Model class representing a book issue/borrow transaction.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class IssuedBook {

    private int    issueId;
    private int    bookId;
    private int    memberId;
    private int    issuedBy;
    private String issueDate;
    private String dueDate;
    private String returnDate;
    private double fineAmount;
    private String status;       // "Issued" | "Returned" | "Overdue"

    // ── Extra display fields (joined from other tables) ───────────────────────
    private String bookTitle;
    private String memberName;
    private String issuedByName;

    // ── Constructors ──────────────────────────────────────────────────────────

    public IssuedBook() {}

    public IssuedBook(int issueId, int bookId, int memberId, int issuedBy,
                      String issueDate, String dueDate, String returnDate,
                      double fineAmount, String status) {
        this.issueId    = issueId;
        this.bookId     = bookId;
        this.memberId   = memberId;
        this.issuedBy   = issuedBy;
        this.issueDate  = issueDate;
        this.dueDate    = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status     = status;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getIssueId()                   { return issueId; }
    public void   setIssueId(int issueId)        { this.issueId = issueId; }

    public int    getBookId()                    { return bookId; }
    public void   setBookId(int bookId)          { this.bookId = bookId; }

    public int    getMemberId()                  { return memberId; }
    public void   setMemberId(int memberId)      { this.memberId = memberId; }

    public int    getIssuedBy()                  { return issuedBy; }
    public void   setIssuedBy(int issuedBy)      { this.issuedBy = issuedBy; }

    public String getIssueDate()                 { return issueDate; }
    public void   setIssueDate(String date)      { this.issueDate = date; }

    public String getDueDate()                   { return dueDate; }
    public void   setDueDate(String date)        { this.dueDate = date; }

    public String getReturnDate()                { return returnDate; }
    public void   setReturnDate(String date)     { this.returnDate = date; }

    public double getFineAmount()                { return fineAmount; }
    public void   setFineAmount(double fine)     { this.fineAmount = fine; }

    public String getStatus()                    { return status; }
    public void   setStatus(String status)       { this.status = status; }

    public String getBookTitle()                 { return bookTitle; }
    public void   setBookTitle(String t)         { this.bookTitle = t; }

    public String getMemberName()                { return memberName; }
    public void   setMemberName(String n)        { this.memberName = n; }

    public String getIssuedByName()              { return issuedByName; }
    public void   setIssuedByName(String n)      { this.issuedByName = n; }
}
