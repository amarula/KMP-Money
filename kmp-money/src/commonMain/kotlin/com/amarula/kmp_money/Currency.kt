package com.amarula.kmp_money

import com.amarula.kmp_money.CurrencyConstants.SYMBOL_POUND
import com.amarula.kmp_money.CurrencyConstants.SYMBOL_YEN
import com.amarula.kmp_money.resources.Res
import com.amarula.kmp_money.resources.currency_aed
import com.amarula.kmp_money.resources.currency_amd
import com.amarula.kmp_money.resources.currency_ang
import com.amarula.kmp_money.resources.currency_aoa
import com.amarula.kmp_money.resources.currency_ars
import com.amarula.kmp_money.resources.currency_aud
import com.amarula.kmp_money.resources.currency_azn
import com.amarula.kmp_money.resources.currency_bam
import com.amarula.kmp_money.resources.currency_bdt
import com.amarula.kmp_money.resources.currency_bgn
import com.amarula.kmp_money.resources.currency_bhd
import com.amarula.kmp_money.resources.currency_bmd
import com.amarula.kmp_money.resources.currency_bnd
import com.amarula.kmp_money.resources.currency_bob
import com.amarula.kmp_money.resources.currency_brl
import com.amarula.kmp_money.resources.currency_bsd
import com.amarula.kmp_money.resources.currency_bwp
import com.amarula.kmp_money.resources.currency_byr
import com.amarula.kmp_money.resources.currency_cad
import com.amarula.kmp_money.resources.currency_chf
import com.amarula.kmp_money.resources.currency_clp
import com.amarula.kmp_money.resources.currency_cny
import com.amarula.kmp_money.resources.currency_cop
import com.amarula.kmp_money.resources.currency_crc
import com.amarula.kmp_money.resources.currency_czk
import com.amarula.kmp_money.resources.currency_dkk
import com.amarula.kmp_money.resources.currency_dop
import com.amarula.kmp_money.resources.currency_dzd
import com.amarula.kmp_money.resources.currency_egp
import com.amarula.kmp_money.resources.currency_etb
import com.amarula.kmp_money.resources.currency_eur
import com.amarula.kmp_money.resources.currency_gbp
import com.amarula.kmp_money.resources.currency_gel
import com.amarula.kmp_money.resources.currency_gtq
import com.amarula.kmp_money.resources.currency_hkd
import com.amarula.kmp_money.resources.currency_hrk
import com.amarula.kmp_money.resources.currency_huf
import com.amarula.kmp_money.resources.currency_idr
import com.amarula.kmp_money.resources.currency_ils
import com.amarula.kmp_money.resources.currency_inr
import com.amarula.kmp_money.resources.currency_iqd
import com.amarula.kmp_money.resources.currency_isk
import com.amarula.kmp_money.resources.currency_jod
import com.amarula.kmp_money.resources.currency_jpy
import com.amarula.kmp_money.resources.currency_kes
import com.amarula.kmp_money.resources.currency_khr
import com.amarula.kmp_money.resources.currency_krw
import com.amarula.kmp_money.resources.currency_kwd
import com.amarula.kmp_money.resources.currency_kzt
import com.amarula.kmp_money.resources.currency_lak
import com.amarula.kmp_money.resources.currency_lbp
import com.amarula.kmp_money.resources.currency_lkr
import com.amarula.kmp_money.resources.currency_ltl
import com.amarula.kmp_money.resources.currency_lvl
import com.amarula.kmp_money.resources.currency_mad
import com.amarula.kmp_money.resources.currency_mdl
import com.amarula.kmp_money.resources.currency_mkd
import com.amarula.kmp_money.resources.currency_mnt
import com.amarula.kmp_money.resources.currency_mop
import com.amarula.kmp_money.resources.currency_mur
import com.amarula.kmp_money.resources.currency_mvr
import com.amarula.kmp_money.resources.currency_mxn
import com.amarula.kmp_money.resources.currency_myr
import com.amarula.kmp_money.resources.currency_mzn
import com.amarula.kmp_money.resources.currency_nad
import com.amarula.kmp_money.resources.currency_ngn
import com.amarula.kmp_money.resources.currency_nok
import com.amarula.kmp_money.resources.currency_nzd
import com.amarula.kmp_money.resources.currency_omr
import com.amarula.kmp_money.resources.currency_pab
import com.amarula.kmp_money.resources.currency_pen
import com.amarula.kmp_money.resources.currency_php
import com.amarula.kmp_money.resources.currency_pkr
import com.amarula.kmp_money.resources.currency_pln
import com.amarula.kmp_money.resources.currency_pyg
import com.amarula.kmp_money.resources.currency_qar
import com.amarula.kmp_money.resources.currency_ron
import com.amarula.kmp_money.resources.currency_rsd
import com.amarula.kmp_money.resources.currency_rub
import com.amarula.kmp_money.resources.currency_sar
import com.amarula.kmp_money.resources.currency_scr
import com.amarula.kmp_money.resources.currency_sek
import com.amarula.kmp_money.resources.currency_sgd
import com.amarula.kmp_money.resources.currency_thb
import com.amarula.kmp_money.resources.currency_tnd
import com.amarula.kmp_money.resources.currency_try
import com.amarula.kmp_money.resources.currency_ttd
import com.amarula.kmp_money.resources.currency_twd
import com.amarula.kmp_money.resources.currency_tzs
import com.amarula.kmp_money.resources.currency_uah
import com.amarula.kmp_money.resources.currency_ugx
import com.amarula.kmp_money.resources.currency_unknown
import com.amarula.kmp_money.resources.currency_usd
import com.amarula.kmp_money.resources.currency_uyu
import com.amarula.kmp_money.resources.currency_vnd
import com.amarula.kmp_money.resources.currency_xaf
import com.amarula.kmp_money.resources.currency_xcd
import com.amarula.kmp_money.resources.currency_xof
import com.amarula.kmp_money.resources.currency_zar
import com.amarula.kmp_money.resources.currency_zmw
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/**
 * Represents a world currency with formatting and display metadata.
 *
 * @property decimalPlaces Number of decimal places used when formatting amounts in this currency.
 * @property currencySymbol The symbol used to represent the currency (e.g. `$`, `€`, `£`).
 * @property symbolIsPrefix `true` if the symbol appears before the amount, `false` if after.
 * @property nameStringResource String resource key for the localised currency name.
 * @property countryCode ISO 3166-1 alpha-2 country code used to resolve the flag image, or empty
 * for supranational currencies.
 */
enum class Currency(
    val decimalPlaces: Int,
    val currencySymbol: String,
    val symbolIsPrefix: Boolean,
    val nameStringResource: StringResource,
    val countryCode: String
) {
    DZD(2, "DA", true, Res.string.currency_dzd, "dz"), // Algerian Dinar
    ARS(2, "$", true, Res.string.currency_ars, "ar"), // Argentine Peso
    AUD(2, "$", true, Res.string.currency_aud, "au"), // Australian Dollar
    BSD(2, "$", true, Res.string.currency_bsd, "bs"), // Bahamian Dollar
    BHD(3, "", true, Res.string.currency_bhd, "bh"), // Bahraini Dinar
    BDT(2, "", true, Res.string.currency_bdt, "bd"), // Bangladesh Taka
    AMD(2, "", true, Res.string.currency_amd, "am"), // Armenian Dram
    BMD(2, "$", true, Res.string.currency_bmd, "bm"), // Bermudian Dollar
    BOB(2, "\$b", true, Res.string.currency_bob, "bo"), // Bolivia Boliviano
    BWP(2, "P", true, Res.string.currency_bwp, "bw"), // Botswana Pula
    BND(2, "$", true, Res.string.currency_bnd, "bn"), // Brunei Dollar
    KHR(2, "", true, Res.string.currency_khr, "kh"), // Cambodia Riel
    CAD(2, "$", true, Res.string.currency_cad, "ca"), // Canadian Dollar
    LKR(2, "Rs", true, Res.string.currency_lkr, "lk"), // Sri Lanka Rupee
    CLP(0, "$", true, Res.string.currency_clp, "cl"), // Chilean Peso
    CNY(2, SYMBOL_YEN, true, Res.string.currency_cny, "cn"), // Chinese Yuan ¥
    COP(2, "$", true, Res.string.currency_cop, "co"), // Colombian Peso
    CRC(2, "C", true, Res.string.currency_crc, "cr"), // Costa Rican Colon
    HRK(2, "kn", true, Res.string.currency_hrk, "hr"), // Croatian Kuna
    CZK(2, "Kč", false, Res.string.currency_czk, "cz"), // Czech Koruna
    DKK(2, "kr.", true, Res.string.currency_dkk, "dk"), // Danish Krone
    DOP(2, "RD$", true, Res.string.currency_dop, "do"), // Dominican Peso
    ETB(2, "Br", true, Res.string.currency_etb, "et"), // Ethiopian Birr
    GTQ(2, "Q", true, Res.string.currency_gtq, "gt"), // Guatemala Quetzal
    HKD(2, "HK$", true, Res.string.currency_hkd, "hk"), // Hong Kong Dollar
    HUF(2, "Ft", true, Res.string.currency_huf, "hu"), // Hungary Forint
    ISK(0, "kr", true, Res.string.currency_isk, "is"), // Iceland Krona
    INR(2, "", true, Res.string.currency_inr, "in"), // Indian Rupee
    IDR(2, "Rp", true, Res.string.currency_idr, "id"), // Indonesia Rupiah
    IQD(3, "", true, Res.string.currency_iqd, "iq"), // Iraqi Dinar
    ILS(2, "", true, Res.string.currency_ils, "il"), // Israeli New Sheqel ₪
    JPY(0, SYMBOL_YEN, true, Res.string.currency_jpy, "jp"), // Japanese Yen ¥
    KZT(2, "T", true, Res.string.currency_kzt, "kz"), // Kazakhstani Tenge
    JOD(3, "", true, Res.string.currency_jod, "jo"), // Jordanian Dinar
    KES(2, "KSh", true, Res.string.currency_kes, "ke"), // Kenyan Shilling
    KRW(0, "W", true, Res.string.currency_krw, "kr"), // Korea Won
    KWD(3, "KD", true, Res.string.currency_kwd, "kw"), // Kuwaiti Dinar
    LAK(2, "", true, Res.string.currency_lak, "la"), // Laos Kip
    LBP(2, SYMBOL_POUND, true, Res.string.currency_lbp, "lb"), // Lebanese Pound
    LVL(2, "Ls", true, Res.string.currency_lvl, "lv"), // Latvian Lats
    LTL(2, "Lt", true, Res.string.currency_ltl, "lt"), // Lithuanian Litas
    MOP(2, "MOP$", true, Res.string.currency_mop, "mo"), // Macau Pataca
    MYR(2, "RM", true, Res.string.currency_myr, "my"), // Malaysian Ringgit
    MVR(2, "Rf", true, Res.string.currency_mvr, "mv"), // Maldivian Rufiyaa
    MUR(2, "Rs", true, Res.string.currency_mur, "mu"), // Mauritius Rupee
    MXN(2, "$", true, Res.string.currency_mxn, "mx"), // Mexican Peso
    MNT(2, "", true, Res.string.currency_mnt, "mn"), // Mongolia Tugrik
    MDL(2, "Lei", true, Res.string.currency_mdl, "md"), // Moldovan Leu
    MAD(2, "DH", true, Res.string.currency_mad, "ma"), // Moroccan Dirham
    OMR(3, "", true, Res.string.currency_omr, "om"), // Omani Rial
    NAD(2, "N$", true, Res.string.currency_nad, "na"), // Namibia Dollar
    ANG(2, "f", true, Res.string.currency_ang, "cw"), // Netherlands Antillean Guilder
    NZD(2, "$", true, Res.string.currency_nzd, "nz"), // New Zealand Dollar
    NGN(2, "", true, Res.string.currency_ngn, "ng"), // Nigeria Naira
    NOK(2, "kr", true, Res.string.currency_nok, "no"), // Norwegian Krone
    PKR(2, "Rs", true, Res.string.currency_pkr, "pk"), // Pakistan Rupee
    PAB(2, "B/.", true, Res.string.currency_pab, "pa"), // Panama Balboa
    PYG(0, "Gs", true, Res.string.currency_pyg, "py"), // Paraguay Guarani
    PEN(2, "S/.", true, Res.string.currency_pen, "pe"), // Peru Nuevo Sol
    PHP(2, "", true, Res.string.currency_php, "ph"), // Philippine Peso ₱
    QAR(2, "", true, Res.string.currency_qar, "qa"), // Qatari Rial
    RUB(2, "", true, Res.string.currency_rub, "ru"), // Russian Rouble ₽
    SAR(2, "", true, Res.string.currency_sar, "sa"), // Saudi Riyal
    SCR(2, "SCR", true, Res.string.currency_scr, "sc"), // Seychelles Rupee
    SGD(2, "$", true, Res.string.currency_sgd, "sg"), // Singapore Dollar
    VND(0, "", true, Res.string.currency_vnd, "vn"), // Vietnamese Dong ₫
    ZAR(2, "R", true, Res.string.currency_zar, "za"), // South Africa Rand
    SEK(2, "kr", true, Res.string.currency_sek, "se"), // Swedish Krona
    CHF(2, "CHF", true, Res.string.currency_chf, "ch"), // Swiss Franc
    THB(2, "", true, Res.string.currency_thb, "th"), // Thailand Baht
    TTD(2, "TT$", true, Res.string.currency_ttd, "tt"), // Trinidad and Tobago Dollar
    AED(2, "", false, Res.string.currency_aed, "ae"), // United Arab Emirates Dirham
    TND(3, "DT", true, Res.string.currency_tnd, "tn"), // Tunisian Dinar
    UGX(2, "USh", true, Res.string.currency_ugx, "ug"), // Uganda Shilling
    MKD(2, "", true, Res.string.currency_mkd, "mk"), // Makedonia Denar
    EGP(2, SYMBOL_POUND, true, Res.string.currency_egp, "eg"), // Egyptian Pound
    GBP(2, SYMBOL_POUND, true, Res.string.currency_gbp, "gb"), // Pound Sterling £
    TZS(2, "TSh", true, Res.string.currency_tzs, "tz"), // Tanzanian Shilling
    USD(2, "$", true, Res.string.currency_usd, "us"), // US Dollar
    UYU(2, "\$U", true, Res.string.currency_uyu, "uy"), // Peso Uruguayo
    TWD(2, "NT$", true, Res.string.currency_twd, "tw"), // New Taiwan Dollar
    RSD(2, "", true, Res.string.currency_rsd, "rs"), // Serbian Dinar
    MZN(2, "MT", true, Res.string.currency_mzn, "mz"), // Mozambique Metical
    AZN(2, "", true, Res.string.currency_azn, "az"), // Azerbaijanian Manat
    RON(2, "lei", false, Res.string.currency_ron, "ro"), // Romanian New Leu
    TRY(2, "Kr", false, Res.string.currency_try, "tr"), // Turkish Lira
    XAF(0, "FCFA", true, Res.string.currency_xaf, "cm"), // CFA Franc BEAC
    XCD(2, "$", true, Res.string.currency_xcd, "ag"), // East Caribbean Dollar
    XOF(0, "CFA", true, Res.string.currency_xof, "sn"), // CFA Franc BCEAO
    ZMW(2, "", true, Res.string.currency_zmw, "zm"), // Zambian Kwacha
    AOA(2, "Kz", true, Res.string.currency_aoa, "ao"), // Angola Kwanza
    BYR(0, "Br", true, Res.string.currency_byr, "by"), // Belarusian Ruble
    BGN(2, "", true, Res.string.currency_bgn, "bg"), // Bulgarian Lev лв
    BAM(2, "KM", true, Res.string.currency_bam, "ba"), // Convertible Mark
    EUR(2, "€", true, Res.string.currency_eur, "eu"), // Euro €
    UAH(2, "", true, Res.string.currency_uah, "ua"), // Ukraina Hryvnia ₴
    GEL(2, "GEL", true, Res.string.currency_gel, "ge"), // Georgia Lari
    PLN(2, "zł", true, Res.string.currency_pln, "pl"), // Polish Zloty ł
    BRL(2, "R$", true, Res.string.currency_brl, "br"), // Brazilian Real
    UNKNOWN(
        2,
        "??",
        false,
        Res.string.currency_unknown,
        ""
    ); // Unknown currency - obsolete or too new

    val flagUrl: String
        get() = if (countryCode.isEmpty()) {
            ""
        } else {
            "https://flagcdn.com/w160/$countryCode.png"
        }

    suspend fun getDisplayName(): String {
        return getString(nameStringResource)
    }

    companion object {
        fun fromName(name: String?): Currency? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
