package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Alex Petrov", "Intern Java Developer", 1, 0));
        save(new Candidate(0, "Werner Siemens", "Junior Java Developer", 2, 0));
        save(new Candidate(0, "Lucas Schmidt", "Junior+ Java Developer", 3, 0));
        save(new Candidate(0, "Steve Jobs", "Middle Java Developer", 1, 0));
        save(new Candidate(0, "Bill Gates", "Middle+ Java Developer", 1, 0));
        save(new Candidate(0, "Andrew Howard", "Senior Java Developer", 2, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> {
            candidate.setCreationDate(oldCandidate.getCreationDate());
            return candidate;
        }) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return List.copyOf(candidates.values());
    }
}
