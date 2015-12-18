package classes

import com.github.xmlgen.annotation.XmlElem
import com.github.xmlgen.annotation.XmlList
import com.github.xmlgen.annotation.XmlType

/**
 * Created by daj on 18/12/2015.
 */
@XmlType
class Customer {

    @XmlElem
    def Name

    @XmlElem
    def age

    @XmlList(item = "account")
    def accounts = []
}
