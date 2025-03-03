INSERT INTO public.organizations
(organization_id, name_organization, little_name_position, name_director, surname_director,
inn, okved, adress, phone_organization, email_organization)
VALUES(0, 'Государственный университет', 'СПб ГКУ', 'Кто-то', 'как-то', 64655, 4664, 'где-то', '5645', 'and@mail.ru');

INSERT INTO public.departments
(department_id, name_department, organization_id)
VALUES(0, 'war', 0);

INSERT INTO public.positions
(position_id, name_position, description_position, department_id)
VALUES(0, 'test', 'test', 0);

INSERT INTO public.myuser
(user_id, username, surname, email, login, "password", phone, position_id, active)
VALUES(0, 'admin', 'admin', 'admin@mail.ru', 'admin', '$2a$10$vRknn.btiovcT/Fm8socC.IRYb.W99vw53qneYRM6ZQ9XAiezFIf2', '456456', 0, true);

INSERT INTO public.user_roles
(role_id, user_id, "role")
VALUES(0, 0, 'ADMIN');