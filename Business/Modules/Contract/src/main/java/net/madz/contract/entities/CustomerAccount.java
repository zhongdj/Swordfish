package net.madz.contract.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;
import net.madz.core.annotations.PinYinIndex;
import net.madz.core.annotations.PinYinIndexed;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name = "customer_account")
@PinYinIndexed
@Indexes({ @Index(name = "INDEX_CUSTOMER_ACCOUNT_SHORT_SEARCH_NAME", columnNames = { "TENANT_ID", "SHORT_PINYIN_ABBR_NAME" }),
        @Index(name = "INDEX_CUSTOMER_ACCOUNT_FULL_SEARCH_NAME", columnNames = { "TENANT_ID", "FULL_PINYIN_ABBR_NAME" }) })
public class CustomerAccount extends StandardObject {

    private static final long serialVersionUID = 1L;
    @Column(name = "FULL_NAME", nullable = false, length = 30)
    private String fullName;
    @Column(name = "SHORT_NAME", length = 10)
    private String shortName;
    @PinYinIndex(from = "shortName")
    @Column(name = "SHORT_PINYIN_ABBR_NAME", length = 10)
    private String shortPinyinAbbrName;
    @PinYinIndex(from = "fullName")
    @Column(name = "FULL_PINYIN_ABBR_NAME", length = 30)
    private String fullPinyinAbbrName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortSearchName() {
        return shortPinyinAbbrName;
    }

    public void setShortSearchName(String shortSearchName) {
        this.shortPinyinAbbrName = shortSearchName;
    }

    public String getFullSearchName() {
        return fullPinyinAbbrName;
    }

    public void setFullSearchName(String fullSearchName) {
        this.fullPinyinAbbrName = fullSearchName;
    }
}
