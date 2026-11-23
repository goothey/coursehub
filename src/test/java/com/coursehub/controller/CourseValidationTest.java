package com.coursehub.controller;

import com.coursehub.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CourseController.class)
class CourseValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseRepository courseRepository;

    @Test
    void emptySubmissionShowsErrorsAndDoesNotSave() throws Exception {
        mockMvc.perform(post("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/add-course"))
                .andExpect(model().attributeHasFieldErrors("course", "title", "category", "difficulty", "durationHours", "price"))
                .andExpect(content().string(containsString("Title is required")));

        verify(courseRepository, never()).save(any());
    }

    @Test
    void badValuesShowClearMessagesAndDoNotSave() throws Exception {
        mockMvc.perform(post("/courses")
                        .param("title", "Hi")            // too short (min 5)
                        .param("category", "PROGRAMMING")
                        .param("difficulty", "BEGINNER")
                        .param("durationHours", "500")   // over 200
                        .param("price", "-5"))           // negative
                .andExpect(status().isOk())
                .andExpect(view().name("courses/add-course"))
                .andExpect(model().attributeHasFieldErrors("course", "title", "durationHours", "price"))
                .andExpect(content().string(containsString("Title must be between 5 and 100 characters")))
                .andExpect(content().string(containsString("Duration must be between 1 and 200 hours")))
                .andExpect(content().string(containsString("Price cannot be negative")));

        verify(courseRepository, never()).save(any());
    }

    @Test
    void validSubmissionSavesAndRedirectsToDetailPage() throws Exception {
        when(courseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/courses")
                        .param("title", "Valid Test Course")
                        .param("category", "MATH")
                        .param("difficulty", "ADVANCED")
                        .param("durationHours", "30")
                        .param("price", "99.00")
                        .param("description", "A course used in the validation test."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/courses/*"));

        verify(courseRepository).save(any());
    }
}
