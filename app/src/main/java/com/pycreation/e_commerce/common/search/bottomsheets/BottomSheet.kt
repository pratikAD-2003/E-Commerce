package com.pycreation.e_commerce.common.search.bottomsheets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.databinding.FragmentBottomSheetBinding
import kotlinx.coroutines.processNextEventInCurrentThread
import java.text.NumberFormat
import java.util.Currency
import kotlin.math.max

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BottomSheet : BottomSheetDialogFragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentBottomSheetBinding

    private val sortByPrice: String = "null"
    private val sortByRatings: String = "null"
    private val brand: String = "null"
    private val minPrice: Int = -1
    private val maxPrice: Int = -1
    private val minRating: Int = -1
    private val maxRating: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        val type = arguments?.getString("type")

        binding.titleTextBtmSheet.text = type

        when (type) {
            "Sort By" -> {
                val innerData = arguments?.getString("inner")
                when (innerData) {
                    "asc" -> {
                        buttonSelected(binding.lowToHighImgSrtByPrice)
                        buttonUnSelected(binding.highToLowImgSrtByPrice)
                        buttonUnSelected(binding.newestImgSrtByPrice)
                        buttonUnSelected(binding.relevanceImgSrtByPrice)
                    }

                    "desc" -> {
                        buttonSelected(binding.highToLowImgSrtByPrice)
                        buttonUnSelected(binding.lowToHighImgSrtByPrice)
                        buttonUnSelected(binding.newestImgSrtByPrice)
                        buttonUnSelected(binding.relevanceImgSrtByPrice)
                    }

                    "newest" -> {
                        buttonSelected(binding.newestImgSrtByPrice)
                        buttonUnSelected(binding.highToLowImgSrtByPrice)
                        buttonUnSelected(binding.lowToHighImgSrtByPrice)
                        buttonUnSelected(binding.relevanceImgSrtByPrice)
                    }
                }
                setupSortByPrice()
                binding.sortByLayoutBtmSheet.visibility = View.VISIBLE
            }

            "Price Range" -> {
                val minPrice = arguments?.getString("innerPMx")
                val maxPrice = arguments?.getString("innerPMi")
                if (minPrice != null && maxPrice != null) {
                    binding.priceSliderBtmSheet.values =
                        listOf(minPrice.toFloat(), maxPrice.toFloat())
                }
                setupPriceSlider()
                binding.priceRangeLayoutBtnSheet.visibility = View.VISIBLE
            }

            "Brand Search" -> {
                binding.brandLayoutBtmSheet.visibility = View.VISIBLE
            }

            "Filter" -> {
//                binding.f.visibility = View.VISIBLE
            }

            "Rating Filter" -> {
                val innerData = arguments?.getString("innerRateData")
                when (innerData) {
                    "asc" -> {
                        buttonSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.oneStarImgBtmSheet)
                        buttonUnSelected(binding.twoStarImgBtmSheet)
                        buttonUnSelected(binding.threeStarImgBtmSheet)
                        buttonUnSelected(binding.fourStarImgBtmSheet)
                        buttonUnSelected(binding.fiveStarImgBtmSheet)
                    }

                    "desc" -> {
                        buttonSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.oneStarImgBtmSheet)
                        buttonUnSelected(binding.twoStarImgBtmSheet)
                        buttonUnSelected(binding.threeStarImgBtmSheet)
                        buttonUnSelected(binding.fourStarImgBtmSheet)
                        buttonUnSelected(binding.fiveStarImgBtmSheet)
                    }

                    "1" -> {
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonSelected(binding.oneStarImgBtmSheet)
                        buttonUnSelected(binding.twoStarImgBtmSheet)
                        buttonUnSelected(binding.threeStarImgBtmSheet)
                        buttonUnSelected(binding.fourStarImgBtmSheet)
                        buttonUnSelected(binding.fiveStarImgBtmSheet)
                    }

                    "2" -> {
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.oneStarImgBtmSheet)
                        buttonSelected(binding.twoStarImgBtmSheet)
                        buttonUnSelected(binding.threeStarImgBtmSheet)
                        buttonUnSelected(binding.fourStarImgBtmSheet)
                        buttonUnSelected(binding.fiveStarImgBtmSheet)
                    }

                    "3" -> {
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.oneStarImgBtmSheet)
                        buttonUnSelected(binding.twoStarImgBtmSheet)
                        buttonSelected(binding.threeStarImgBtmSheet)
                        buttonUnSelected(binding.fourStarImgBtmSheet)
                        buttonUnSelected(binding.fiveStarImgBtmSheet)
                    }

                    "4" -> {
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.oneStarImgBtmSheet)
                        buttonUnSelected(binding.twoStarImgBtmSheet)
                        buttonUnSelected(binding.threeStarImgBtmSheet)
                        buttonSelected(binding.fourStarImgBtmSheet)
                        buttonUnSelected(binding.fiveStarImgBtmSheet)
                    }

                    "5" -> {
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.highToLowRatImgBtmSheet)
                        buttonUnSelected(binding.lowToHighRatImgBtmSheet)
                        buttonUnSelected(binding.relavanceRatImgBtmSheet)
                        buttonUnSelected(binding.oneStarImgBtmSheet)
                        buttonUnSelected(binding.twoStarImgBtmSheet)
                        buttonUnSelected(binding.threeStarImgBtmSheet)
                        buttonUnSelected(binding.fourStarImgBtmSheet)
                        buttonSelected(binding.fiveStarImgBtmSheet)
                    }

                }
                setupRating()
                binding.sortByRatingLayoutBtmSheet.visibility = View.VISIBLE
            }
        }

        binding.priceSliderBtmSheet.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("INR")
            format.format(value.toDouble())
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = BottomSheet().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    private fun buttonSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.oval_red)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.black_outlined))
    }

    private fun buttonUnSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.white_oval)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.grey_outlined))
    }

    private fun setupPriceSlider() {
        binding.applyPriceRangeBtnBtmSheet.setOnClickListener {
            val values = binding.priceSliderBtmSheet.values
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("priceRangeMin", values[0].toString())
            intent.putExtra("priceRangeMax", values[1].toString())
            activity?.sendBroadcast(intent)
            dismiss()
        }
    }

    private fun setupSortByPrice() {
        binding.relevanceSortByPriceBtmSheet.setOnClickListener {
            buttonSelected(binding.relevanceImgSrtByPrice)
            buttonUnSelected(binding.newestImgSrtByPrice)
            buttonUnSelected(binding.lowToHighImgSrtByPrice)
            buttonUnSelected(binding.highToLowImgSrtByPrice)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortPrice", "relevance")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.newestSortByPriceBtmSheet.setOnClickListener {
            buttonSelected(binding.newestImgSrtByPrice)
            buttonUnSelected(binding.relevanceImgSrtByPrice)
            buttonUnSelected(binding.lowToHighImgSrtByPrice)
            buttonUnSelected(binding.highToLowImgSrtByPrice)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortPrice", "newest")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.highToLowSortByPriceBtmSheet.setOnClickListener {
            buttonSelected(binding.highToLowImgSrtByPrice)
            buttonUnSelected(binding.newestImgSrtByPrice)
            buttonUnSelected(binding.relevanceImgSrtByPrice)
            buttonUnSelected(binding.lowToHighImgSrtByPrice)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortPrice", "HighToLow")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.lowToHighSortByPriceBtmSheet.setOnClickListener {
            buttonSelected(binding.lowToHighImgSrtByPrice)
            buttonUnSelected(binding.highToLowImgSrtByPrice)
            buttonUnSelected(binding.newestImgSrtByPrice)
            buttonUnSelected(binding.relevanceImgSrtByPrice)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortPrice", "lowToHigh")
            activity?.sendBroadcast(intent)
            dismiss()
        }
    }

    private fun setupRating() {

        binding.oneStarRatBtmSheet.setOnClickListener {
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "1")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.twoStarRatBtmSheet.setOnClickListener {
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "2")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.threeStarRatBtmSheet.setOnClickListener {
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "3")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.fourStarRatBtmSheet.setOnClickListener {
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "4")
            activity?.sendBroadcast(intent)
            dismiss()
        }
        binding.fiveStarRatBtmSheet.setOnClickListener {
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonSelected(binding.fiveStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "5")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.relevanceRatBtmSheet.setOnClickListener {
            buttonSelected(binding.relavanceRatImgBtmSheet)
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "relevance")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.highToLowRatBtmSheet.setOnClickListener {
            buttonSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonUnSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "HighToLow")
            activity?.sendBroadcast(intent)
            dismiss()
        }

        binding.lowToHighRatBtmSheet.setOnClickListener {
            buttonSelected(binding.lowToHighRatImgBtmSheet)
            buttonUnSelected(binding.highToLowRatImgBtmSheet)
            buttonUnSelected(binding.relavanceRatImgBtmSheet)
            buttonUnSelected(binding.oneStarImgBtmSheet)
            buttonUnSelected(binding.twoStarImgBtmSheet)
            buttonUnSelected(binding.threeStarImgBtmSheet)
            buttonUnSelected(binding.fourStarImgBtmSheet)
            buttonUnSelected(binding.fiveStarImgBtmSheet)
            val intent = Intent("BROADCAST_BOTTOM_SHEET")
            intent.putExtra("sortRating", "lowToHigh")
            activity?.sendBroadcast(intent)
            dismiss()
        }
    }
}