package com.example.wgutscheduler.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "assessment",
        foreignKeys = @ForeignKey(
                entity = Course.class,
                parentColumns = "course_id",
                childColumns = "course_id_fk",
                onDelete = CASCADE
        )
)
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    private int assessment_id;
    @ColumnInfo(name = "course_id_fk")
    private int course_id_fk;
    @ColumnInfo(name = "assessment_name")
    private String assessment_name;
    @ColumnInfo(name = "assessment_type")
    private String assessment_type;
    @ColumnInfo(name = "assessment_status")
    private String assessment_status;
    @ColumnInfo(name = "assessment_due_date")
    private Date assessment_due_date;
    @ColumnInfo(name = "assessment_alert")
    private boolean assessment_alert;

    public int getAssessment_id() {
        return assessment_id;
    }

    public void setAssessment_id(int assessment_id) {
        this.assessment_id = assessment_id;
    }

    public int getCourse_id_fk() {
        return course_id_fk;
    }

    public void setCourse_id_fk(int course_id_fk) {
        this.course_id_fk = course_id_fk;
    }

    public String getAssessment_name() {
        return assessment_name;
    }

    public void setAssessment_name(String assessment_name) {
        this.assessment_name = assessment_name;
    }

    public String getAssessment_type() {
        return assessment_type;
    }

    public void setAssessment_type(String assessment_type) {
        this.assessment_type = assessment_type;
    }

    public String getAssessment_status() {
        return assessment_status;
    }

    public void setAssessment_status(String assessment_status) {
        this.assessment_status = assessment_status;
    }

    public Date getAssessment_due_date() {
        return assessment_due_date;
    }

    public void setAssessment_due_date(Date assessment_due_date) {
        this.assessment_due_date = assessment_due_date;
    }

    public boolean getAssessment_alert() {
        return assessment_alert;
    }

    public void setAssessment_alert(boolean assessment_alert) {
        this.assessment_alert = assessment_alert;
    }

    @Override
    public String toString() {
        return this.getAssessment_name();
    }
}
