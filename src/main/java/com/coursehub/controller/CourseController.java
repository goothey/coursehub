package com.coursehub.controller;

import com.coursehub.model.Category;
import com.coursehub.model.Course;
import com.coursehub.model.Difficulty;
import com.coursehub.repository.CourseRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CourseController {

    private static final int PAGE_SIZE = 6;
    private static final List<String> ALLOWED_SORTS = List.of("title", "price");

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Browse page: filter by category and difficulty, sort, and paginate
    @GetMapping("/courses")
    public String browse(@RequestParam(required = false) Category category,
                         @RequestParam(required = false) Difficulty difficulty,
                         @RequestParam(defaultValue = "title") String sortBy,
                         @RequestParam(defaultValue = "asc") String dir,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {

        // Only allow sorting by fields we expect, so users cannot sort by anything else
        if (!ALLOWED_SORTS.contains(sortBy)) {
            sortBy = "title";
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Build the filter conditions. Each one is only added if the user chose it.
        List<Specification<Course>> filters = new ArrayList<>();
        if (category != null) {
            filters.add((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (difficulty != null) {
            filters.add((root, query, cb) -> cb.equal(root.get("difficulty"), difficulty));
        }
        Specification<Course> spec = Specification.allOf(filters);

        // The database does the sorting and paging, not Java code
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(direction, sortBy));
        Page<Course> result = courseRepository.findAll(spec, pageRequest);

        model.addAttribute("page", result);
        model.addAttribute("categories", Category.values());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", direction.name().toLowerCase());
        return "courses/browse";
    }

    // Show the empty Add Course form
    @GetMapping("/courses/new")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        addDropdownOptions(model);
        return "courses/add-course";
    }

    // Receive the form, validate it, save it, then redirect to the detail page
    @PostMapping("/courses")
    public String saveCourse(@Valid @ModelAttribute("course") Course course,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDropdownOptions(model);
            return "courses/add-course";
        }
        Course saved = courseRepository.save(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course added successfully!");
        return "redirect:/courses/" + saved.getId();
    }

    // Show one saved course
    @GetMapping("/courses/{id:\\d+}")
    public String showCourse(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        model.addAttribute("course", course);
        return "courses/course-detail";
    }

    private void addDropdownOptions(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("difficulties", Difficulty.values());
    }
}
