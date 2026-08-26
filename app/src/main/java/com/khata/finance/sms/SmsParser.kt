package com.khata.finance.sms

import com.khata.finance.data.TxnType
import java.util.regex.Pattern

data class ParsedSms(
    val amount: Double,
    val type: TxnType,
    val merchant: String?,
    val balance: Double?
)

/**
 * Parses Meezan Bank (and generally most Pakistani bank) transaction alert SMS.
 *
 * IMPORTANT: I don't have a live sample of your exact Meezan alert text, so this
 * uses the wording patterns common to Pakistani bank alerts (Rs./PKR, "debited",
 * "credited", "Avl Bal"). If your real alerts phrase things differently, any SMS
 * that comes from a recognized sender but fails to parse gets queued in the
 * "Review SMS" screen so you never lose a transaction — from there you can see
 * the raw text and tell me the exact wording so I can tighten these patterns.
 */
object SmsParser {

    // Add/adjust sender IDs here if your alerts come from a different ID than these.
    val KNOWN_SENDERS = listOf("MEEZAN", "MEEZANBANK", "MEBL", "MEZN", "8258")

    fun isFromBank(sender: String): Boolean {
        val s = sender.uppercase()
        return KNOWN_SENDERS.any { s.contains(it) }
    }

    // Matches: Rs.1,500.00 / Rs 1500 / PKR 1,500.00
    private val amountPattern = Pattern.compile(
        "(?:Rs\\.?|PKR)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )
    private val debitWords = listOf("debited", "withdrawn", "spent", "purchase", "debit", "paid")
    private val creditWords = listOf("credited", "deposited", "received", "credit")

    private val merchantPattern = Pattern.compile(
        "(?:at|to)\\s+([A-Za-z0-9 &.'_-]{3,30})",
        Pattern.CASE_INSENSITIVE
    )

    private val balancePattern = Pattern.compile(
        "(?:Avl\\.?\\s*Bal(?:ance)?|Available\\s*Balance)[:\\s]*(?:Rs\\.?|PKR)?\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(body: String): ParsedSms? {
        val lower = body.lowercase()
        val isDebit = debitWords.any { lower.contains(it) }
        val isCredit = creditWords.any { lower.contains(it) }
        if (!isDebit && !isCredit) return null

        val amounts = mutableListOf<Double>()
        val m = amountPattern.matcher(body)
        while (m.find()) {
            m.group(1).replace(",", "").toDoubleOrNull()?.let { amounts.add(it) }
        }
        if (amounts.isEmpty()) return null

        val balMatch = balancePattern.matcher(body)
        val balance = if (balMatch.find()) balMatch.group(1).replace(",", "").toDoubleOrNull() else null

        // Heuristic: the transaction amount is normally the first amount mentioned;
        // balance (matched separately above) is excluded from this pick where possible.
        val txnAmount = amounts.firstOrNull { it != balance } ?: amounts.first()

        val merchantMatch = merchantPattern.matcher(body)
        val merchant = if (merchantMatch.find()) merchantMatch.group(1).trim() else null

        return ParsedSms(
            amount = txnAmount,
            type = if (isCredit) TxnType.INCOME else TxnType.EXPENSE,
            merchant = merchant,
            balance = balance
        )
    }
}
