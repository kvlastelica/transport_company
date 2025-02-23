import { IUser } from 'app/entities/user/user.model';
import { IDepartment } from 'app/entities/department/department.model';
import { IRoute } from 'app/entities/route/route.model';

export interface IEmployee {
  id: number;
  employeCode?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  jobTitle?: string | null;
  user?: Pick<IUser, 'id'> | null;
  department?: IDepartment | null;
  routes?: IRoute[] | null;
}

export type NewEmployee = Omit<IEmployee, 'id'> & { id: null };
