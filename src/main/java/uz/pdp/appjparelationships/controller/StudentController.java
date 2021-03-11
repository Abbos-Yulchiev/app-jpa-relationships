package uz.pdp.appjparelationships.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDTO;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    final StudentRepository studentRepository;
    final AddressRepository addressRepository;
    final GroupRepository groupRepository;

    public StudentController(StudentRepository studentRepository, AddressRepository addressRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.groupRepository = groupRepository;
    }

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping(value = "/group/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                @RequestParam Integer page) {

        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupId, pageable);
    }

    @PostMapping()
    public String addStudent(@RequestBody StudentDTO studentDTO) {

        Optional<Address> optionalAddress = addressRepository.findById(studentDTO.getAddressId());
        Optional<Group> optionalGroup = groupRepository.findById(studentDTO.getGroupId());

        if (optionalAddress.isPresent() && optionalGroup.isPresent()) {

            Student newStudent = new Student();
            newStudent.setFirstName(studentDTO.getFirstName());
            newStudent.setLastName(studentDTO.getLastName());
            newStudent.setAddress(optionalAddress.get());
            newStudent.setGroup(optionalGroup.get());
            newStudent.setSubjects(studentDTO.getSubjects());

            return "New Student saved";
        }
        return "Address or Group  not found";
    }

    @PutMapping(value = "/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDTO studentDTO) {

        Optional<Student> studentOptional = studentRepository.findById(id);

        if (studentOptional.isPresent()) {

            Optional<Address> optionalAddress = addressRepository.findById(studentDTO.getAddressId());
            Optional<Group> optionalGroup = groupRepository.findById(studentDTO.getGroupId());

            if (optionalAddress.isPresent() && optionalGroup.isPresent()) {

                Student editStudent = studentOptional.get();
                editStudent.setFirstName(studentDTO.getFirstName());
                editStudent.setLastName(studentDTO.getLastName());
                editStudent.setAddress(optionalAddress.get());
                editStudent.setGroup(optionalGroup.get());
                editStudent.setSubjects(studentDTO.getSubjects());
                return "Student edited";
            }
            return "Address or Group  not found";
        }
        return "Student not found";
    }

    @DeleteMapping(value = "/{id}")
    public String deleteStudent(@PathVariable Integer id) {

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            studentRepository.deleteById(id);
            return "Student deleted";
        }
        return "Student not found";
    }
}
