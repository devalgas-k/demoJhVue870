import { type IDepartment } from '@/shared/model/department.model';

import { type Contract } from '@/shared/model/enumerations/contract.model';
export interface IEmployee {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string;
  phoneNumber?: string | null;
  hireDate?: Date | null;
  salary?: number | null;
  commissionPct?: number | null;
  level?: number | null;
  contract?: keyof typeof Contract | null;
  cvContentType?: string | null;
  cv?: string | null;
  manager?: IEmployee | null;
  department?: IDepartment | null;
}

export class Employee implements IEmployee {
  constructor(
    public id?: number,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string,
    public phoneNumber?: string | null,
    public hireDate?: Date | null,
    public salary?: number | null,
    public commissionPct?: number | null,
    public level?: number | null,
    public contract?: keyof typeof Contract | null,
    public cvContentType?: string | null,
    public cv?: string | null,
    public manager?: IEmployee | null,
    public department?: IDepartment | null,
  ) {}
}
