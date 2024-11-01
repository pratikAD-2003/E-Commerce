package com.pycreation.e_commerce.consumer.dashboard.tabs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.common.adaptors.ImageSliderAdapter
import com.pycreation.e_commerce.common.search.SearchProList
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.adaptors.HomeImageSliderAdapter
import com.pycreation.e_commerce.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Home : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding
    private var autoSlideJob: Job? = null
    private var currentPage = 1
    private lateinit var adapter : HomeImageSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setImageAdapter(
            listOf(
//                "http://192.168.96.145:3030/uploads/products/productImages_1729160151312.jpeg",
//                "http://192.168.96.145:3030/uploads/products/productImages_1729088378975.jpeg",
//                "http://192.168.96.145:3030/uploads/products/productImages_1729160151312.jpeg",
//                "http://192.168.96.145:3030/uploads/products/productImages_1729088378975.jpeg"
                resources.getDrawable(R.drawable.banner_3),
                resources.getDrawable(R.drawable.banner_5),
                resources.getDrawable(R.drawable.banner_3s),
                resources.getDrawable(R.drawable.banner),
                resources.getDrawable(R.drawable.banner_2)
            )
        )

        binding.searchBarHome.setOnClickListener {
            (activity as ConsumerDashboard?)?.startActivity(
                Intent(
                    requireContext(),
                    SearchProList::class.java
                )
            )
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setImageAdapter(images: List<Any>) {
        adapter = HomeImageSliderAdapter(images)
        binding.homeImgViewPager.adapter = adapter

        createDots(images.size, 0) // Initialize dots
        binding.homeImgViewPager.setCurrentItem(1, false)
        currentPage = 1

        binding.homeImgViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val itemCount = adapter.itemCount

                // Handle infinite scroll by switching to the real item
                when (position) {
                    0 -> {
                        // If we're on the "fake" last page, jump to the real last page
                        binding.homeImgViewPager.setCurrentItem(itemCount - 2, false)
                        currentPage = itemCount - 2
                    }

                    itemCount - 1 -> {
                        // If we're on the "fake" first page, jump to the real first page
                        binding.homeImgViewPager.setCurrentItem(1, false)
                        currentPage = 1
                    }

                    else -> {
                        currentPage = position
                        updateDots(position - 1) // Update dots accordingly (adjust for "fake" items)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Stop auto-slide when the user manually interacts with the ViewPager
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    autoSlideJob?.cancel()
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    startAutoSlide(adapter) // Resume auto-slide after user stops interacting
                }
            }
        })

        startAutoSlide(adapter)

        binding.homeImgViewPager.setPageTransformer { page, position ->
//            page.translationX = -position * page.width  // Custom slide effect
//            page.alpha = 0.5f + (1 - abs(position))  // Fade effect
            when {
                position <= -1 -> {
                    // Page is off-screen to the left
                    page.alpha = 0f
                }

                position <= 0 -> {
                    // This page is coming in (the previous page)
                    page.alpha = 1f + position // Fades in from the left
                    page.translationX = 0f // No translation, keeps it in the center
                    page.scaleY = 1f // Keep original scale
                }

                position <= 1 -> {
                    // This page is going out (the current page)
                    page.alpha = 1 - abs(position) // Fades out to the right
                    page.translationX = -position * page.width // Standard slide transition
                    page.scaleY = 1f // Keep original scale
                }

                else -> {
                    // Page is off-screen to the right
                    page.alpha = 0f
                }
            }
        }
    }

    private fun startAutoSlide(adapter: HomeImageSliderAdapter) {
        // Cancel any existing autoSlideJob before starting a new one
        autoSlideJob?.cancel()

        autoSlideJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(6000) // Delay for 3 seconds
                currentPage++
                if (currentPage >= adapter.itemCount) {
                    currentPage = 1 // Wrap around
                }
                binding.homeImgViewPager.setCurrentItem(currentPage, true)
            }
        }
    }

    private fun createDots(size: Int, currentPosition: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        binding.dotsIndicatorHomeScreen.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(this.context).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (i == currentPosition) R.drawable.active_dot else R.drawable.inactive_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                binding.dotsIndicatorHomeScreen.addView(this, params)
            }
        }
    }

    private fun updateDots(position: Int) {
        val childCount = binding.dotsIndicatorHomeScreen.childCount
        for (i in 0 until childCount) {
            val imageView = binding.dotsIndicatorHomeScreen.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == position) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        startAutoSlide(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideJob?.cancel() // Cancel the auto-slide when the fragment is not visible
    }


    override fun onStop() {
        super.onStop()
        autoSlideJob?.cancel() // Cancel the auto-slide when the fragment is not visible
    }
}