--
-- PostgreSQL database dump
--

-- Started on 2026-08-07 21:40:19



--
-- TOC entry 223 (class 1259 OID 16984)
-- Name: boards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.boards (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    name character varying(255),
    project_id uuid
);



--
-- TOC entry 222 (class 1259 OID 16969)
-- Name: projects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projects (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    description character varying(255),
    name character varying(255),
    workspace_id uuid
);



--
-- TOC entry 220 (class 1259 OID 16918)
-- Name: tasks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tasks (
    id uuid NOT NULL,
    title character varying(255),
    description character varying(255),
    status character varying(255),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    user_id uuid,
    due_date date,
    priority character varying(255),
    board_id uuid,
    assigned_member_id uuid,
    CONSTRAINT tasks_priority_check CHECK (((priority)::text = ANY ((ARRAY['HIGH'::character varying, 'MEDIUM'::character varying, 'LOW'::character varying, 'URGENT'::character varying])::text[]))),
    CONSTRAINT tasks_status_check CHECK (((status)::text = ANY ((ARRAY['TODO'::character varying, 'IN_PROGRESS'::character varying, 'IN_REVIEW'::character varying, 'COMPLETED'::character varying])::text[])))
);



--
-- TOC entry 219 (class 1259 OID 16909)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    email character varying(255),
    full_name character varying(255),
    password character varying(255),
    profile_image_url character varying(255),
    status character varying(255),
    updated_at timestamp(6) without time zone,
    CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'BLOCKED'::character varying, 'DELETED'::character varying])::text[])))
);



--
-- TOC entry 224 (class 1259 OID 17006)
-- Name: workspace_members; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.workspace_members (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    joined_at timestamp(6) without time zone NOT NULL,
    role character varying(255) NOT NULL,
    invited_by uuid,
    user_id uuid NOT NULL,
    workspace_id uuid NOT NULL,
    CONSTRAINT workspace_members_role_check CHECK (((role)::text = ANY ((ARRAY['OWNER'::character varying, 'ADMIN'::character varying, 'MEMBER'::character varying, 'VIEWER'::character varying])::text[])))
);


--
-- TOC entry 221 (class 1259 OID 16932)
-- Name: workspaces; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.workspaces (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    description character varying(255),
    name character varying(255),
    user_id uuid NOT NULL
);



--
-- TOC entry 4787 (class 2606 OID 16989)
-- Name: boards boards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.boards
    ADD CONSTRAINT boards_pkey PRIMARY KEY (id);


--
-- TOC entry 4785 (class 2606 OID 16978)
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- TOC entry 4781 (class 2606 OID 16923)
-- Name: tasks tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);


--
-- TOC entry 4789 (class 2606 OID 17020)
-- Name: workspace_members uk_workspace_user; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspace_members
    ADD CONSTRAINT uk_workspace_user UNIQUE (workspace_id, user_id);


--
-- TOC entry 4779 (class 2606 OID 16917)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4791 (class 2606 OID 17018)
-- Name: workspace_members workspace_members_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspace_members
    ADD CONSTRAINT workspace_members_pkey PRIMARY KEY (id);


--
-- TOC entry 4783 (class 2606 OID 16942)
-- Name: workspaces workspaces_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspaces
    ADD CONSTRAINT workspaces_pkey PRIMARY KEY (id);


--
-- TOC entry 4797 (class 2606 OID 16992)
-- Name: boards fk32qdrlyxkxq7cm48pviayc3e7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.boards
    ADD CONSTRAINT fk32qdrlyxkxq7cm48pviayc3e7 FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- TOC entry 4792 (class 2606 OID 16927)
-- Name: tasks fk6s1ob9k4ihi75xbxe2w0ylsdh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT fk6s1ob9k4ihi75xbxe2w0ylsdh FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4798 (class 2606 OID 17026)
-- Name: workspace_members fk6vtnpc3eexk504u61uepn40p1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspace_members
    ADD CONSTRAINT fk6vtnpc3eexk504u61uepn40p1 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4793 (class 2606 OID 17041)
-- Name: tasks fk_task_assigned_member; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT fk_task_assigned_member FOREIGN KEY (assigned_member_id) REFERENCES public.workspace_members(id) ON DELETE SET NULL;


--
-- TOC entry 4799 (class 2606 OID 17021)
-- Name: workspace_members fkcyc1ucl6nuti00kavnp8wwuwb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspace_members
    ADD CONSTRAINT fkcyc1ucl6nuti00kavnp8wwuwb FOREIGN KEY (invited_by) REFERENCES public.users(id);


--
-- TOC entry 4795 (class 2606 OID 16943)
-- Name: workspaces fkfwu53godully4c0ppjg0hhy3s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspaces
    ADD CONSTRAINT fkfwu53godully4c0ppjg0hhy3s FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4794 (class 2606 OID 16998)
-- Name: tasks fkitp79nb81vimv715wd9t8cjmf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT fkitp79nb81vimv715wd9t8cjmf FOREIGN KEY (board_id) REFERENCES public.boards(id);


--
-- TOC entry 4796 (class 2606 OID 16979)
-- Name: projects fkpc7qv7bnsq7dm17g0tb0a60of; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT fkpc7qv7bnsq7dm17g0tb0a60of FOREIGN KEY (workspace_id) REFERENCES public.workspaces(id);


--
-- TOC entry 4800 (class 2606 OID 17031)
-- Name: workspace_members fkw9hq87n3rvq2c4j47qo78i5r; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.workspace_members
    ADD CONSTRAINT fkw9hq87n3rvq2c4j47qo78i5r FOREIGN KEY (workspace_id) REFERENCES public.workspaces(id);


-- Completed on 2026-08-07 21:40:20

--
-- PostgreSQL database dump complete
--
