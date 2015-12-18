package classes

import com.github.xmlgen.annotation.XmlElem
import com.github.xmlgen.annotation.XmlType

/**
 * Created by et0s on 18/12/2015.
 */
@XmlType
class Account {

    @XmlElem
    def Name

    @XmlElem
    def Balance
}
